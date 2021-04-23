#!/bin/bash
#
# This script is utilized to publish the release to GitHub
#

# Declare cURL Headers
CURL_ACCEPT_HEADER="Accept: application/vnd.github.v3+json"
CURL_AUTH_HEADER="Authorization: token ${GITHUB_TOKEN}"

# Extract changelog for release from CHANGELOG.md
RELEASE_BODY=$(sed -n "/^## \[${CIRCLE_TAG}\]/,/^## /p" CHANGELOG.md | grep -E "^(-|###)")

# Declare cURL data
CURL_DATA='{"tag_name": "'"${CIRCLE_TAG}"'", "name":"'"Taplytics ${CIRCLE_TAG} Release"'", "body": "'"${RELEASE_BODY}"'", "target_commitish": "'"${CIRCLE_SHA1}"'"}'

# Remove newlines from CURL_DATA since RELEASE_BODY might contain some
echo -n "$CURL_DATA" >dirty-curl-data.json
sed '$!s/$/\\n/' dirty-curl-data.json | tr -d '\n' >clean-curl-data.json

curl "https://api.github.com/repos/taplytics/taplytics-react-native/releases" -X POST -H "$CURL_ACCEPT_HEADER" -H "$CURL_AUTH_HEADER" --data "@clean-curl-data.json"
curl "https://api.github.com/repos/taplytics/${CIRCLE_PROJECT_REPONAME}/releases" -X POST -H "$CURL_ACCEPT_HEADER" -H "$CURL_AUTH_HEADER" --data "@clean-curl-data.json"
