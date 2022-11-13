#/bin/bash

for KILLPID in `ps ax | grep 'exec.sh' | awk ' {print $1;}'`; do 
  kill -9 $KILLPID;
done

for KILLPID in `ps ax | grep 'memoryController' | awk ' {print $1;}'`; do 
  kill -9 $KILLPID;
done
