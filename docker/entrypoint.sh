#!/bin/sh
set -e

# Templates come from a /conf mount if one is given, otherwise from the ones baked into the image.
# Env vars are substituted into /conf-resolved so a mounted /conf stays untouched.
if [ -n "$(ls -A /conf 2>/dev/null)" ]; then
    TEMPLATE_DIR=/conf
else
    TEMPLATE_DIR="$CONF_TEMPLATE_DIR"
    # Every ${VAR} in the baked templates is required.
    for v in $(cat "$TEMPLATE_DIR"/* | grep -o '\${[A-Za-z_][A-Za-z0-9_]*}' | tr -d '${}' | sort -u); do
        eval "[ -n \"\${$v}\" ]" || { echo "$v must be set" >&2; exit 1; }
    done
fi

mkdir -p /conf-resolved
for f in "$TEMPLATE_DIR"/*.properties "$TEMPLATE_DIR"/*.xml; do
    [ -f "$f" ] || continue
    envsubst < "$f" > "/conf-resolved/$(basename "$f")"
done

exec "$@"
