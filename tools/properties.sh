#!/bin/bash

set -e

VERSIONS_DIR="versions"
DRY_RUN=false

while [[ "$1" == --* ]]; do
    case "$1" in
        --dry) DRY_RUN=true ;;
        *) echo "Unknown flag: $1" && exit 1 ;;
    esac
    shift
done

ACTION="$1" # add/edit/remove
KEY="$2"
VALUE="$3" # value to add/edit

if [[ "$ACTION" != "add" && "$ACTION" != "edit" && "$ACTION" != "remove" ]]; then
    echo "Usage: $0 [--dry] <add|edit|remove> <key> [value]"
    exit 1
fi

if [[ "$ACTION" != "remove" && -z "$VALUE" ]]; then
    echo "You must provide a value for 'add' or 'edit'."
    exit 1
fi

for props in "$VERSIONS_DIR"/*/gradle.properties; do
    echo "Processing $props"

    if $DRY_RUN; then
        echo "$ACTION $KEY in $props"
        cat "$props"
        continue
    fi

    case "$ACTION" in
        add)
            if ! grep -q "^$KEY=" "$props"; then
                if [[ -s "$props" && "$(tail -c1 "$props")" != "" ]]; then
                    echo "" >> "$props"
                fi
                echo "$KEY=$VALUE" >> "$props"
                echo "Added $KEY=$VALUE to $props"
            else
                echo "Skipped: $KEY already exists in $props"
            fi
            ;;
        edit)
            if grep -q "^$KEY=" "$props"; then
                sed -i.bak "s|^$KEY=.*|$KEY=$VALUE|" "$props" && rm "$props.bak"
                echo "Edited $KEY=$VALUE in $props"
            else
                if [[ -s "$props" && "$(tail -c1 "$props")" != "" ]]; then
                    echo "" >> "$props"
                fi
                echo "$KEY=$VALUE" >> "$props"
                echo "Added $KEY=$VALUE to $props (did not exist)"
            fi
            ;;
        remove)
            sed -i.bak "/^$KEY=/d" "$props" && rm "$props.bak"
            echo "Removed $KEY from $props"
            ;;
    esac
done

echo "Done. ${DRY_RUN:+(Dry run only)}"