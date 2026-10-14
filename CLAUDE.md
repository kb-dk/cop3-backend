# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

cop3-backend is the COP REST backend (Jersey 2 on Tomcat, Hibernate 5 + PostGIS, SolrJ 9, Java 11). See `../CLAUDE.md` for the monorepo overview and `DEVELOPER.md` for the ER diagram and API docs link.

## Build & test

- `mvn clean package` builds `target/cop3-backend.war` and, through the assembly plugin (`src/main/assembly/cop-jar.xml`), `target/cop3-backend-cop-jar.jar`.
- Single test: `mvn -Dtest=ValidatePathParamTest test`, or one method with `-Dtest=ValidatePathParamTest#methodName`.
- The surefire plugin is not pinned, so Maven falls back to its default (2.12.4), which predates JUnit 5. Results are unreliable: e.g. `ValidatePathParamTest` (JUnit 5, 7 `@Test` methods) reports "Tests run: 0". Never read "0 failures" as passing; check the `Tests run` count in `target/surefire-reports/`.
- Most tests are integration tests and need live services (see `src/test/resources/cop_config.xml` and `hibernate.cfg.xml`):
  - Postgres at `localhost:25432`, db `cop` (missing DB → ~60s connect timeout, then NPE in `TestUtil.openDatabaseSession`)
  - Solr at `localhost:50006`
  - `ApiTest`/`CrashTest` additionally make HTTP calls to a running backend at `cop2_backend.baseurl`; no embedded server is started
  Use `-DskipTests` when these aren't up (as `deploy2devel.sh` does).
- The monorepo `cop-docker` exposes Postgres on 55432 and Solr on 50001; the `cop_config.xml` files here assume 25432/50006. Align them before running integration tests.
- No formatter or linter is configured.

## Release & deploy

- Releases go through `maven-release-plugin` (commit pairs "prepare release cop3-backend-X" / "prepare for next development iteration"). Add an entry to `changelog.txt` (this repo has no CHANGELOG.md) before releasing.
- `./deploy2devel.sh` builds with `-DskipTests` and scp's the war to cop-devel-01 as `cop.war`.

## Runtime configuration

- `listeners/InitializationContextListener` runs at startup. It reads JNDI `java:/comp/env/copBackendProperties` (path to a properties XML), falling back to classpath `/cop_config.xml`, and passes it to `constants/CopBackendProperties`. Logback config comes from JNDI `logbackConfigFile` or the env var of the same name; a `logback-test.xml` on the classpath short-circuits this.
- `crud/database/HibernateUtil` loads `hibernate.cfg.xml` and overrides URL/user/password from `CopBackendProperties`. Entity mappings are `.hbm.xml` files under `src/main/resources/dk/kb/cop3/backend/`, not annotations. Entity classes live in `crud/database/hibernate`.
- Property keys still carry the `cop2_backend.*` prefix from the cop2 era.

## Architecture

- **Routing**: `web.xml` maps Jersey (`jersey.config.server.provider.packages = dk.kb.cop3.backend.crud.api`) to `/*`, and OCLC OAICat (`ORG.oclc.oai.server.OAIHandler`, config `oaicat.properties`) to `/oai/*`. The OAI catalog and crosswalks are in `crud/oai/server`.
- **Read path**: an `api/*Service` resource uses a `MetadataSource` (`SolrMetadataSource` for search, `HibernateEditionSource` and the DAOs for direct DB reads) and hands the result to a `format/*MetadataFormulator` (MODS, Atom, RSS, KML, OSD, Solr, …), which renders the requested format.
- **Write path**: services use a `MetadataWriter` (`HibernateMetadataWriter`). Incoming MODS is mapped to a Hibernate `Object` by `crud/util/ObjectFromModsExtractor`. Partial updates go through `crud/update/Reformulator`, which applies `reformulate-*.xsl` to the stored MODS.
- **MODS is canonical.** All output formats and Solr documents come from XSLT in `src/main/resources` (`mods2*.xsl`, `build_*.xsl` + `template_*.xml`, `mods_solrizr.xsl`). A format change usually means editing XSLT, not Java. `trace-transformations=true` in the config logs the Solr transform (`SolrMetadataFormulator` only).
- **Solr**: `solr/CopSolrClient`. The schema used by this repo's tests is in `src/cop/conf`; the runtime configset is in the monorepo `cop-docker/configsets/`.
- **Spatial**: PostGIS points via `PostgreSQL95DialectWithPoint` and `database/type/Point`. Area lookups use `constants/Areas` / `DSFLAreas` and `AreasInDk`.
- **Transactions**: services open Hibernate sessions directly. Make sure every path, including error paths, closes the session/transaction (see the 3.0.14 fix in `EditionService`).
- **Legacy Oracle**: `migrate/`, `resources/oracle/`, `Oracle10gGeometryDialect`, `JGeometryType` and the ojdbc/sdoapi deps exist only for the one-off Oracle→Postgres migration. Run a migration with `java -cp target/cop3-backend-cop-jar.jar dk.kb.cop3.backend.migrate.<Class>`. Don't build new features on them.
- `scripts/` (`UpdateIndex`, `DeleteObjects`) are standalone maintenance mains run from the fat jar.
