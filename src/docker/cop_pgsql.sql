--
-- PostgreSQL database dump
--

-- Dumped from database version 12.12 (Debian 12.12-1.pgdg110+1)
-- Dumped by pg_dump version 12.12 (Ubuntu 12.12-0ubuntu0.20.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: audit_trail; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.audit_trail (
    id character varying(1024) NOT NULL,
    oid character varying(1024) NOT NULL,
    eid character varying(256),
    mods character varying(255),
    last_modified character varying(1024) NOT NULL,
    deleted character(1) NOT NULL,
    last_modified_by character varying(4000) NOT NULL,
    obj_version numeric(22,0) NOT NULL,
    point uuid
);


ALTER TABLE public.audit_trail OWNER TO cop3;

--
-- Name: category; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.category (
    id character varying(1024) NOT NULL,
    category_text character varying(4000) NOT NULL
);


ALTER TABLE public.category OWNER TO cop3;

--
-- Name: category_join; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.category_join (
    oid character varying(1024) NOT NULL,
    cid character varying(1024) NOT NULL
);


ALTER TABLE public.category_join OWNER TO cop3;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.comments (
    id character varying(50) NOT NULL,
    comment_text character varying(2048) NOT NULL,
    creator character varying(100) NOT NULL,
    xlink_to character varying(200) NOT NULL
);


ALTER TABLE public.comments OWNER TO cop3;

--
-- Name: edition; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.edition (
    id character varying(1024) NOT NULL,
    name character varying(4000) NOT NULL,
    name_en character varying(4000) NOT NULL,
    url_name character varying(4000) NOT NULL,
    url_matrial_type character varying(1024) NOT NULL,
    url_pub_year numeric(22,0) NOT NULL,
    url_pub_month character varying(1024) NOT NULL,
    url_collection character varying(1024) NOT NULL,
    cumulus_catalog character varying(4000) NOT NULL,
    cumulus_top_catagory character varying(1024) NOT NULL,
    normalisationrule character varying(1024) NOT NULL,
    status character varying(1024),
    ui_language character varying(1024),
    ui_sort character varying(1024),
    ui_show character varying(1024),
    opml character varying(255),
    description character varying(4000),
    description_en character varying(4000),
    collection_da character varying(4000),
    collection_en character varying(4000),
    department_da character varying(4000),
    department_en character varying(4000),
    contact_email character varying(4000),
    last_modified character varying(1024),
    visible_to_public character(1),
    log character varying(999999)
);


ALTER TABLE public.edition OWNER TO cop3;

--
-- Name: object; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.object (
    id character varying(1024) NOT NULL,
    type_id numeric(22,0) NOT NULL,
    eid character varying(1024) NOT NULL,
    mods character varying(255) NOT NULL,
    last_modified character varying(1024) NOT NULL,
    deleted character(1) NOT NULL,
    last_modified_by character varying(4000) NOT NULL,
    obj_version numeric(22,0) NOT NULL,
    title character varying(4000),
    creator character varying(4000),
    bookmark numeric(19,2) DEFAULT 0,
    likes numeric(22,0) DEFAULT 0,
    correctness numeric(22,0) DEFAULT 0.0,
    random_number numeric(22,0) NOT NULL,
    interestingess numeric(22,0) NOT NULL,
    person character varying(4000),
    building character varying(4000),
    location character varying(4000),
    not_before date,
    not_after date
);


ALTER TABLE public.object OWNER TO cop3;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.tag (
    id character varying(1024) NOT NULL,
    tag_value character varying(4000) NOT NULL
);


ALTER TABLE public.tag OWNER TO cop3;

--
-- Name: tag_join; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.tag_join (
    oid character varying(1024) NOT NULL,
    tid character varying(1024) NOT NULL
);


ALTER TABLE public.tag_join OWNER TO cop3;

--
-- Name: typeOracle; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.typeOracle (
    id numeric(22,0) NOT NULL,
    type_text character varying(4000) NOT NULL
);


ALTER TABLE public.typeOracle OWNER TO cop3;

--
-- Name: user_permissions; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.user_permissions (
    permission_id integer NOT NULL,
    permission_name character varying(255) NOT NULL
);


ALTER TABLE public.user_permissions OWNER TO cop3;

--
-- Name: user_role; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.user_role (
    role_id integer NOT NULL,
    role_name character varying(255)
);


ALTER TABLE public.user_role OWNER TO cop3;

--
-- Name: user_role_permissions; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.user_role_permissions (
    role_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.user_role_permissions OWNER TO cop3;

--
-- Name: users; Type: TABLE; Schema: public; Owner: cop3
--

CREATE TABLE public.users (
    user_pid character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    user_given_name character varying(255) NOT NULL,
    user_surname character varying(255) NOT NULL,
    user_common_name character varying(255) NOT NULL,
    user_role_id integer,
    user_email character varying(255),
    user_score numeric(19,2) DEFAULT 0,
    userscore1 numeric(19,2) DEFAULT 0,
    userscore2 numeric(19,2) DEFAULT 0,
    userscore3 numeric(19,2) DEFAULT 0,
    userscore4 numeric(19,2) DEFAULT 0,
    userscore5 numeric(19,2) DEFAULT 0,
    userscore6 numeric(19,2) DEFAULT 0,
    userscore7 numeric(19,2) DEFAULT 0,
    userscore8 numeric(19,2) DEFAULT 0,
    userscore9 numeric(19,2) DEFAULT 0,
    last_active_date timestamp without time zone NOT NULL
);


ALTER TABLE public.users OWNER TO cop3;

--
-- Data for Name: audit_trail; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.audit_trail (id, oid, eid, mods, last_modified, deleted, last_modified_by, obj_version, point) FROM stdin;
\.


--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.category (id, category_text) FROM stdin;
\.


--
-- Data for Name: category_join; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.category_join (oid, cid) FROM stdin;
\.


--
-- Data for Name: comments; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.comments (id, comment_text, creator, xlink_to) FROM stdin;
\.


--
-- Data for Name: edition; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.edition (id, name, name_en, url_name, url_matrial_type, url_pub_year, url_pub_month, url_collection, cumulus_catalog, cumulus_top_catagory, normalisationrule, status, ui_language, ui_sort, ui_show, opml, description, description_en, collection_da, collection_en, department_da, department_en, contact_email, last_modified, visible_to_public, log) FROM stdin;
\.


--
-- Data for Name: object; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.object (id, type_id, eid, mods, last_modified, deleted, last_modified_by, obj_version, title, creator, bookmark, likes, correctness, random_number, interestingess, person, building, location, not_before, not_after) FROM stdin;
\.


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.tag (id, tag_value) FROM stdin;
\.


--
-- Data for Name: tag_join; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.tag_join (oid, tid) FROM stdin;
\.


--
-- Data for Name: typeOracle; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.typeOracle (id, type_text) FROM stdin;
\.


--
-- Data for Name: user_permissions; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.user_permissions (permission_id, permission_name) FROM stdin;
\.


--
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.user_role (role_id, role_name) FROM stdin;
\.


--
-- Data for Name: user_role_permissions; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.user_role_permissions (role_id, permission_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: cop3
--

COPY public.users (user_pid, user_id, user_given_name, user_surname, user_common_name, user_role_id, user_email, user_score, userscore1, userscore2, userscore3, userscore4, userscore5, userscore6, userscore7, userscore8, userscore9, last_active_date) FROM stdin;
\.


--
-- Name: audit_trail audit_trail_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.audit_trail
    ADD CONSTRAINT audit_trail_pkey PRIMARY KEY (id);


--
-- Name: category_join category_join_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.category_join
    ADD CONSTRAINT category_join_pkey PRIMARY KEY (cid, oid);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: edition edition_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.edition
    ADD CONSTRAINT edition_pkey PRIMARY KEY (id);


--
-- Name: object object_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT object_pkey PRIMARY KEY (id);


--
-- Name: tag_join tag_join_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.tag_join
    ADD CONSTRAINT tag_join_pkey PRIMARY KEY (tid, oid);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- Name: typeOracle type_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.typeOracle
    ADD CONSTRAINT type_pkey PRIMARY KEY (id);


--
-- Name: typeOracle uk_e5aewgfmk92uay33rfbe9dcqp; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.typeOracle
    ADD CONSTRAINT uk_e5aewgfmk92uay33rfbe9dcqp UNIQUE (type_text);


--
-- Name: user_permissions user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.user_permissions
    ADD CONSTRAINT user_permissions_pkey PRIMARY KEY (permission_id);


--
-- Name: user_role_permissions user_role_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.user_role_permissions
    ADD CONSTRAINT user_role_permissions_pkey PRIMARY KEY (permission_id, role_id);


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (role_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_pid);


--
-- Name: tag_join fkbxf43dfdb0tqssmfckj20twhp; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.tag_join
    ADD CONSTRAINT fkbxf43dfdb0tqssmfckj20twhp FOREIGN KEY (tid) REFERENCES public.tag(id);


--
-- Name: tag_join fkc9qb3s27krw79djj61a6nf21j; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.tag_join
    ADD CONSTRAINT fkc9qb3s27krw79djj61a6nf21j FOREIGN KEY (oid) REFERENCES public.object(id);


--
-- Name: category_join fkia5q347wuajs2h8uytmk40f3j; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.category_join
    ADD CONSTRAINT fkia5q347wuajs2h8uytmk40f3j FOREIGN KEY (oid) REFERENCES public.object(id);


--
-- Name: object fklxmnwmmudbfr1h1gbyeq3p8ky; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT fklxmnwmmudbfr1h1gbyeq3p8ky FOREIGN KEY (type_id) REFERENCES public.typeOracle(id);


--
-- Name: category_join fko9kk9okw6rcogu5a1oy3wts01; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.category_join
    ADD CONSTRAINT fko9kk9okw6rcogu5a1oy3wts01 FOREIGN KEY (cid) REFERENCES public.category(id);


--
-- Name: object fksh3u3082gt0yf1m4g1ik8rb5a; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT fksh3u3082gt0yf1m4g1ik8rb5a FOREIGN KEY (eid) REFERENCES public.edition(id);


--
-- Name: comments fktigg7pcjgq8l9ti54blv9m2s; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fktigg7pcjgq8l9ti54blv9m2s FOREIGN KEY (xlink_to) REFERENCES public.object(id);


--
-- Name: user_role_permissions permission_id; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.user_role_permissions
    ADD CONSTRAINT permission_id FOREIGN KEY (permission_id) REFERENCES public.user_permissions(permission_id);


--
-- Name: user_role_permissions role_id; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.user_role_permissions
    ADD CONSTRAINT role_id FOREIGN KEY (role_id) REFERENCES public.user_role(role_id);


--
-- Name: users role_id; Type: FK CONSTRAINT; Schema: public; Owner: cop3
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT role_id FOREIGN KEY (user_role_id) REFERENCES public.user_role(role_id);


--
-- PostgreSQL database dump complete
--

