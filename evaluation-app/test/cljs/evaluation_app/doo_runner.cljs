(ns evaluation-app.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [evaluation-app.core-test]))

(doo-tests 'evaluation-app.core-test)

