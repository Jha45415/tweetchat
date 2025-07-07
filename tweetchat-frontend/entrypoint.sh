#!/bin/sh


set -e

echo "--- Running entrypoint.sh ---"
echo "VITE_API_URL is: ${VITE_API_URL}"

ROOT_DIR=/usr/share/nginx/html


find $ROOT_DIR/assets -type f -name "*.js" -print0 | while IFS= read -r -d '' file;
do
  echo "Processing file: $file"

  sed -i 's|__VITE_API_URL__|'${VITE_API_URL}'|g' "$file"
done

echo "Placeholder replacement finished."
echo "Starting Nginx..."


exec "$@"