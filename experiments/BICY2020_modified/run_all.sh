#in git root folder execute the following commands:

source "experiments/BICY2020_modified/run_set_variables.sh"

RUN_ALL="scripts/circe_cluster/run_all_batches.sh"


[ -z "$DO_EXPERIMENT_1" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment1-Traces.csv     $LOG_E1 10
[ -z "$DO_EXPERIMENT_2" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment2-SingleMin.csv  $LOG_E2 10
[ -z "$DO_EXPERIMENT_3" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment3-SingleSame.csv $LOG_E3 4
[ -z "$DO_EXPERIMENT_4" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment4-extraAtFeeder.csv $LOG_E4 10
[ -z "$DO_EXPERIMENT_5" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment5-density.csv       $LOG_E5 8
[ -z "$DO_EXPERIMENT_6" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment6-extraAtGap.csv    $LOG_E6 10
[ -z "$DO_EXPERIMENT_7" ] || sh $RUN_ALL $CONFIGS_FOLDER/experiment7-nonUniform.csv    $LOG_E7 25
# sh $RUN_ALL ${CONFIG_FOLDER}/experiment4-Mazes.csv ${LOG_FOLDER}experiment4-mazes 100
# sh $RUN_ALL ${CONFIG_FOLDER}/experiment5-Single.csv ${LOG_FOLDER}experiment5-single 100
# sh $RUN_ALL ${CONFIG_FOLDER}/experiment5-TwoScales.csv ${LOG_FOLDER}experiment5-twoScales 100