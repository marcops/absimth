#!/bin/bash
SEED=$1;
echo "seed = ${SEED}"
#return 0

FOLDER_BASE="/home/marco/pucrs/absimth/"
PROGRAM="${FOLDER_BASE}absimth/build/libs/absimth.jar"
FOLDER_PATH=$(pwd)"/"

FAULT_INJECTION=$(find . -maxdepth 1 -type f -not -path '*/\.*' -name "*.yml" | sed 's/^\.\///g' | rev | cut -c 5- | rev)
MEMORY_CONTROLLER=("HammingMemoryController" "DFTMemoryController" "ReedSolomonMemoryController")

function process_bins() {
    BINS="$1"
    #for SEED in {1..10}; do
    for BIN in ${BINS[@]}; do
        for FAULT in ${FAULT_INJECTION[@]}; do
            for MEMCTL in ${MEMORY_CONTROLLER[@]}; do
                echo "RUNNING ${SEED} ${BIN} - ${FAULT} - ${MEMCTL}"
                java -jar ${PROGRAM} --folder=${FOLDER_PATH} --filename=${FAULT}".yml" --outputFilename=${SEED}"-"${BIN}"-"${FAULT}"-"${MEMCTL}"-" --memoryController=${MEMCTL} --programsToLoad=${BIN} --seed=${SEED} &>> exec.txt
            done
        done
    done
    #done
}

#process all bins on folder
BINS_ON_FOLDER=$(find . -maxdepth 1 -type f -not -path '*/\.*' -name "*.bin" | sed 's/^\.\///g' | rev | cut -c 5- | rev)
process_bins "${BINS_ON_FOLDER[@]}"

#process multiple bins on same processor or in diff
BINS_TO_RUN=("bubbleSort-0,crc8-0" 
    "bubbleSort-0,crc8-1" 
    "bubbleSort-0,crc8-0,bubbleSort-1,bubbleSort-2"
)
process_bins "${BINS_TO_RUN[*]}"

rm exec.txt