#!/bin/bash
#
# This script is utilized to trigger the integratin tests
#

# Define Circle API
CIRCLE_API="https://circleci.com/api/v2"

# Declare body used for triggering Pipeline request
TRIGGER_PIPELINE_DATA="{ \"branch\": \"${INTEGRATION_PROJECT_BRANCH}\", \"parameters\": { \"taplytcs-react-native-private-branch\": \"${CIRCLE_BRANCH}\"} }"
printf "Triggering Pipeline with data:"
echo -e "  $TRIGGER_PIPELINE_DATA"

# Declare URL used to trigger Pipeline
TRIGGER_PIPELINE_URL="${CIRCLE_API}/project/gh/${CIRCLE_PROJECT_USERNAME}/${INTEGRATION_PROJECT_REPONAME}/pipeline"
printf "\n\nRequesting %s" "${TRIGGER_PIPELINE_URL}"

# Make cURL call to trigger Pipeline
TRIGGER_PIPELINE_RESPONSE=$(curl -s -u "${CIRCLE_TOKEN}": -o trigger-response.json -w "%{http_code}" -X POST --header "Content-Type: application/json" -d "$TRIGGER_PIPELINE_DATA" "$TRIGGER_PIPELINE_URL")

# If the request was successful
if [[ $TRIGGER_PIPELINE_RESPONSE =~ 2[0-9]{2} ]]; then

  # Print Response
  printf "\n\nTrigger Pipeline API Call Succeeded"
  printf "\nResponse:"
  cat trigger-response.json

  # Extract Pipeline build ID and number
  BUILD_ID=$(jq -r ".id" trigger-response.json)

  # Extract Pipeline state
  PIPELINE_STATE=$(jq -r ".state" trigger-response.json)

  # Fetch Pipeline details again
  GET_PIPELINE_URL="${CIRCLE_API}/pipeline/${BUILD_ID}"
  printf "\n\nRequesting call to %s" "${GET_PIPELINE_URL}"

  while [[ $PIPELINE_STATE == 'pending' ]]; do

    sleep 10

    GET_PIPELINE_RESPONSE=$(curl -s -u "${CIRCLE_TOKEN}": -o trigger-response.json -w "%{http_code}" -X GET "$GET_PIPELINE_URL")

    # If request to get pipeline details was successfull
    if [[ $GET_PIPELINE_RESPONSE =~ 2[0-9]{2} ]]; then

      # Extract Pipeline state
      PIPELINE_STATE=$(jq -r ".state" trigger-response.json)

      printf "\n\nCurrent state of pipeline: %s" "${PIPELINE_STATE}"

    else

      printf "\n\nUnable to fetch status of pipeline"
      exit 1
    fi

  done

  printf "\n\nChecking status of workflow..."

  # Declare URL for getting Pipeline workflows
  GET_PIPELINE_WORKFLOW_URL="${CIRCLE_API}/pipeline/${BUILD_ID}/workflow"
  printf "\n\nRequesting call to %s" "${GET_PIPELINE_WORKFLOW_URL}"

  # Execute cURL call to get Pipeline workflow details
  GET_PIPELINE_WORKFLOW_RESPONSE=$(curl -s -u "${CIRCLE_TOKEN}": -o pipeline-response.json -w "%{http_code}" -X GET "$GET_PIPELINE_WORKFLOW_URL")

  # If request to get pipeline workflow was successfull
  if [[ $GET_PIPELINE_WORKFLOW_RESPONSE =~ 2[0-9]{2} ]]; then

    # Extract workflow status and ID
    WORKFLOW_STATUS=$(jq -r ".items[0].status" pipeline-response.json)

    # While the workflow is still running, keep fetching the status
    while [[ $WORKFLOW_STATUS == 'running' ]]; do

      # Wait so that we don't bombard the API
      sleep 30

      # Re-execute the request to fetch workflow details
      GET_PIPELINE_WORKFLOW_RESPONSE=$(curl -s -u "${CIRCLE_TOKEN}": -o pipeline-response.json -w "%{http_code}" -X GET "$GET_PIPELINE_WORKFLOW_URL")

      # If re-fetching workflow details is successful
      if [[ $GET_PIPELINE_WORKFLOW_RESPONSE =~ 2[0-9]{2} ]]; then

        # Extract workflow status and print
        WORKFLOW_STATUS=$(jq -r ".items[0].status" pipeline-response.json)
        printf "\n\nWorkflow status after re-fetching: %s | Tests still running..." "${WORKFLOW_STATUS}"

      else

        printf "\n\nUnable to fetch status of workflow"
        exit 1
      fi

    done

    # Print status of the workflow
    case $WORKFLOW_STATUS in
    'success')
      printf "\n\nTests Passed! 🙌 🔥"
      ;;
    'failing')
      printf "\n\nTests %s ❌" "${WORKFLOW_STATUS}"
      exit 1
      ;;
    'cancelled')
      printf "\n\nTests %s 🚫" "${WORKFLOW_STATUS}"
      exit 1
      ;;
    *)
      printf "\n\nTest status: %s" "${WORKFLOW_STATUS}"
      exit 1
      ;;
    esac

  else

    printf "\n\nUnable to fetch status of workflow"
    exit 1
  fi

else

  # In the situation that the request to trigger the job failed, print response
  printf "\n\nReceived status code: %s", "${TRIGGER_PIPELINE_RESPONSE}"
  printf "\nResponse:"
  cat trigger-response.json
  exit 1

fi
