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

(def plate-thickness 5.5)
(def hole-thickness (+ plate-thickness 0.2)) ;just needs to be bigger than plate-thickness


;---------------------------------------------------

(def single-hole
  (cube keyswitch-width keyswitch-height hole-thickness))

(def single-solid-plate
  (cube (+ choc-cap-width space-between-caps) (+ choc-cap-height space-between-caps) plate-thickness))

(def single-hole-spaced
  (difference single-solid-plate
              single-hole))

(defn duplicate-in-column
  [thing key-index mapval]
  (translate [0 (* key-index (+ choc-cap-height space-between-caps)) 0] thing))

(defn column-plate
  [thing key-count]
  (union (map-indexed (partial duplicate-in-column thing) (repeat key-count 0))))

(defn translate-thing-by-offset
  [thing column-index offset]
  (translate [(* column-index (+ choc-cap-width space-between-caps)) offset 0] thing))


;---------------------------------------------------
; THUMB CLUSTER
;---------------------------------------------------
(def thumb-offsets-not-touching ;probably not parameterizing...just manually fiddling :-(
  [{:x -38 :y -30 :rotation 65}
   {:x -23 :y -11 :rotation 35}
   {:x -1.5 :y -2 :rotation 10}
   {:x 19.5 :y 0 :rotation 0}
   {:x 41.25 :y -3 :rotation -15}
   ])

(def thumb-offsets ;probably not parameterizing...just manually fiddling :-(
  [{:x -37 :y -28 :rotation 65}
   {:x -22 :y -10.5 :rotation 35}
   {:x -1.25 :y -1.75 :rotation 10}
   {:x 19.5 :y 0 :rotation 0}
   {:x 41.5 :y -6 :rotation -30}
   ])

(defn translate-and-rotate-thing
  [thing column-index offset]
  (->> thing
       (rotate (deg->rad (offset :rotation)) [0 0 1] )
       (translate [(offset :x ) (offset :y) 0])
       ))

(def right-thumb-hulled
  (difference
    (hull (map-indexed (partial translate-and-rotate-thing single-solid-plate) thumb-offsets))
    (map-indexed (partial translate-and-rotate-thing single-hole) thumb-offsets)))

(def right-thumb
  (difference
    (union (map-indexed (partial translate-and-rotate-thing single-solid-plate) thumb-offsets))
    (map-indexed (partial translate-and-rotate-thing single-hole) thumb-offsets)))

(def right-thumb-carve
  (translate [15 -60 0] (binding [*fn* 200 ](cylinder 50 10)))) ; binding the fn for the number of segments in the cylinder

(def right-thumb-test
  (difference right-thumb-hulled
              right-thumb-carve)) ; binding the fn for the number of segments in the cylinder

(def right-thumb-translation [(* 1 (+ choc-cap-width space-between-caps))
                              (* -0.9 (+ choc-cap-height space-between-caps))
                              0])


;---------------------------------------------------

(def right-fingers-homerow
  (union (map-indexed (partial translate-thing-by-offset single-hole-spaced) column-offsets)))

(def right-fingers
  (difference 
    (union (map-indexed (partial translate-thing-by-offset (column-plate single-solid-plate key-count-y)) column-offsets))
    (map-indexed (partial translate-thing-by-offset (column-plate single-hole key-count-y)) column-offsets)))

(def right-hand
  (union right-fingers (translate right-thumb-translation right-thumb)))

(def right-plate-hulled
  (hull (translate right-thumb-translation  (map-indexed (partial translate-and-rotate-thing single-solid-plate) thumb-offsets))
        (union (map-indexed (partial translate-thing-by-offset (column-plate single-solid-plate key-count-y)) column-offsets))
        ))

(def right-plate-carved-hull
  (difference right-plate-hulled
              (translate right-thumb-translation right-thumb-carve)
              (translate [-210 38 0] (binding [*fn* 200 ](cylinder 200 10))) ; binding the fn for the number of segments in the cylinder
              (translate [100 -62 0] (binding [*fn* 200 ](cylinder 45 10))))) ; binding the fn for the number of segments in the cylinder

(def right-holes
  (union
    (translate right-thumb-translation (map-indexed (partial translate-and-rotate-thing single-hole) thumb-offsets))
    (map-indexed (partial translate-thing-by-offset (column-plate single-hole key-count-y)) column-offsets)))

(def right-hand-hulled
  (difference right-plate-carved-hull right-holes))

(def right-fingers-off-center
  (translate [40 0 0 ]
             right-fingers))

(def left-fingers-off-center
  (mirror [1 0 0] right-fingers-off-center))

(def joined-board
  (union (map #(rotate %2 [0 0 1] %)
              [left-fingers-off-center  right-fingers-off-center]
              [(* -1 (/ π 6)) (* 1 (/ π 6))]))) ; using radians, unicode for pi is 03c0,  linux ctrl+shift+u then unicod  or windows alt code is 227

(def key-space-test
  (union (map-indexed (partial translate-thing-by-offset (column-plate single-hole-spaced 2)) [0 0])))


(spit "output/plate.scad"
      (write-scad right-hand-hulled))
      ;(write-scad right-thumb-test))
      ;(write-scad right-thumb))
      ;(write-scad joined-board))
