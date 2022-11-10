#!/bin/bash

for SEED in {1..10}; do
    bash exec.sh ${SEED} &
done
