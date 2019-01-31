(ns case.plate
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all])
  (:gen-class))

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def sa-profile-key-height 12.7)
(def choc-cap-width 17)
(def space-between-caps 3)

(def plate-thickness 6)
(def hole-thickness (+ plate-thickness 0.2)) ;just needs to be bigger than plate-thickness
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))

(def column-offsets [0 2 8 1 -4 -5])

(def old-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                      (translate [0
                                  (+ (/ 1.5 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                       (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 1])
                      (hull (->> (cube 1.5 2.75 plate-thickness)
                                 (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                             0
                                             (/ plate-thickness 2)]))))
        plate-half (union top-wall left-wall (with-fn 100 side-nub))]
    (union plate-half
           (->> plate-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(def single-hole
  (cube keyswitch-height keyswitch-width hole-thickness))

(def single-solid-plate
  (cube (+ choc-cap-width space-between-caps) (+ choc-cap-width space-between-caps) plate-thickness))

(def single-plate-with-nubs
  (let [side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 -1])
                      (hull (->> (cube 1 2.75 plate-thickness)
                                 (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                             0
                                             0]))))
        side-nubs (union (with-fn 100 side-nub)
                         (->> (with-fn 100 side-nub)
                              (mirror [1 0 0])
                              (mirror [0 1 0])))]

    (union (difference single-solid-plate
                       single-hole)
           side-nubs)
    ))

(def solid-plate
  (cube 35 17 plate-thickness))

(def single-hole-spaced
  (difference single-solid-plate
              single-hole))

(def column-plate
  (union single-hole-spaced
         (translate [(+ choc-cap-width space-between-caps) 0 0] single-hole-spaced)
         (translate [(- 0 (+ choc-cap-width space-between-caps)) 0 0] single-hole-spaced)))

(def left-fingers-plate
  (union (map-indexed #(translate [%2 (* % (+ choc-cap-width space-between-caps)) 0] single-hole-spaced) column-offsets)))
  


(spit "output/plate.scad"
      (write-scad left-fingers-plate))
