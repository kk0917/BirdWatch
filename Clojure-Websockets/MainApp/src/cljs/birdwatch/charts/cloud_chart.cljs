(ns birdwatch.charts.cloud-chart
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [birdwatch.util :as util]
            [cljs.core.async :as async :refer [put! chan sub]]))

(enable-console-print!)

;;; WordCloud element (implemented externally in JavaScript)
(def cloud-elem (util/by-id "wordCloud"))
(def w (util/elem-width cloud-elem))

(defn mount-wordcloud
  "Mount wordcloud and wire channels for incoming data and outgoing commands."
  [state-pub cmd-chan]
  (let [on-click #(put! cmd-chan [:append-search-text %])
        word-cloud (.WordCloud js/BirdWatch w (* w 0.7) 250 on-click cloud-elem)
        sub-chan (chan)]
    (go-loop []
             (let [[_ words] (<! sub-chan)]
               (.redraw word-cloud (clj->js words))
               (recur)))
    (sub state-pub :words-cloud sub-chan)))