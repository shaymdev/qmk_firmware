(ns case.plate
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all])
  (:gen-class))

(def key-count-y 3)
(def column-offsets [0 2 8 1 -7 -8]) ;the number of values in this vector determin key-count-x or the number of keys in the horizontal "row"

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def choc-cap-height 17)
(def choc-cap-width 18)
(def space-between-caps 1.5)

(def plate-thickness 6)
(def hole-thickness (+ plate-thickness 0.2)) ;just needs to be bigger than plate-thickness


;---------------------------------------------------

(def single-hole
  (cube keyswitch-height keyswitch-width hole-thickness))

(def single-solid-plate
  (cube (+ choc-cap-height space-between-caps) (+ choc-cap-width space-between-caps) plate-thickness))

(def single-hole-spaced
  (difference single-solid-plate
              single-hole))

(defn duplicate-in-column
  [thing key-index mapval]
  (translate [(* key-index (+ choc-cap-height space-between-caps)) 0 0] thing))

(defn column-plate
  [key-count]
  (union (map-indexed (partial duplicate-in-column single-hole-spaced) (repeat key-count 0))))

(defn translate-thing-by-offset
  [thing column-index offset]
  (translate [offset (* column-index (+ choc-cap-width space-between-caps)) 0] thing))


;---------------------------------------------------

(def left-fingers-homerow
  (union (map-indexed (partial translate-thing-by-offset single-hole-spaced) column-offsets)))

(def left-fingers
  (union (map-indexed (partial translate-thing-by-offset (column-plate key-count-y)) column-offsets)))

(def left-fingers-off-center
  (translate [0 40 0 ]
             left-fingers))

(def right-fingers-off-center
  (mirror [0 1 0] left-fingers-off-center))

(def joined-board
  (union (map #(rotate %2 [0 0 1] %)
              [right-fingers-off-center  left-fingers-off-center]
              [(/ π 6) (* -1 (/ π 6)) #_(/ π (/ 5 6))]))) ; using radians, unicode for pi is 03c0,  linux ctrl+shift+u then unicod  or windows alt code is 227

(def key-space-test
  (union (map-indexed (partial translate-thing-by-offset (column-plate 2)) [0 0])))


(spit "output/plate.scad"
      (write-scad key-space-test))
      ;(write-scad joined-board))
