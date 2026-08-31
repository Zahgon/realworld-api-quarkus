#!/bin/bash
java -jar target/realworld-api-spring.jar > service.log &
SERVICE_PROCESS=$!
# Wait until the application reports that it finished starting. The log file is polled
# instead of piping `tail -f` into `grep -q`, because on BSD/macOS `tail -f` block-buffers
# when its stdout is a pipe, so `grep -q` never observes the line and the script hangs.
STARTUP_TIMEOUT_SECONDS=120
for _ in $(seq 1 $STARTUP_TIMEOUT_SECONDS); do
  if grep -q 'Started RealworldApiApplication' service.log 2>/dev/null; then
    break
  fi
  if ! kill -0 $SERVICE_PROCESS 2>/dev/null; then
    echo "Application process exited before startup completed" >&2
    cat service.log >&2
    exit 1
  fi
  sleep 1
done

if ! grep -q 'Started RealworldApiApplication' service.log 2>/dev/null; then
  echo "Application did not start within ${STARTUP_TIMEOUT_SECONDS}s" >&2
  cat service.log >&2
  kill $SERVICE_PROCESS
  exit 1
fi

echo "Application started"
./collections/run-api-tests.sh
kill $SERVICE_PROCESS
rm service.log
