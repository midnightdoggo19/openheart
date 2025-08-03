#!/bin/bash

set -e

VERSIONS_DIR="versions"
DRY_RUN=false
RAW=false

while [[ "$1" == --* ]]; do
    case "$1" in
        --dry) DRY_RUN=true ;;
        --raw) RAW=true ;;
        *) echo "Unknown flag: $1" && exit 1 ;;
    esac
    shift
done

ACTION="$1" # add/edit/remove
KEY="$2" # dot path
VALUE="$3" # value to add/edit

if [[ "$ACTION" != "add" && "$ACTION" != "edit" && "$ACTION" != "remove" ]]; then
    echo "Usage: $0 [--dry] [--raw] <add|edit|remove> <key> [value]"
    exit 1
fi

if [[ "$ACTION" != "remove" && -z "$VALUE" ]]; then
    echo "You must provide a value for 'add' or 'edit'."
    exit 1
fi

for json in "$VERSIONS_DIR"/*/fabric.mod.json; do
    echo "Processing $json"

    # build jq expression
    case "$ACTION" in
        add|edit)
            if $RAW; then
                JQ_EXPR=".$KEY = $VALUE"
            else
                JQ_EXPR=".$KEY = \"$VALUE\""
            fi
            ;;
        remove)
            JQ_EXPR="del(.$KEY)"
            ;;
    esac

    if $DRY_RUN; then
        echo "jq '$JQ_EXPR'"
        jq "$JQ_EXPR" "$json"
    else
        jq "$JQ_EXPR" "$json" > "$json.tmp" && mv "$json.tmp" "$json"
        echo "Modified $json"
    fi
done

echo "Done. ${DRY_RUN:+(Dry run only)}"