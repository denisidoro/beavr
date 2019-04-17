(ns beavr.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [beavr.integration]))

(doo-tests 'beavr.integration)
