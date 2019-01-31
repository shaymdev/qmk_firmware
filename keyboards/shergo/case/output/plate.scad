union () {
  difference () {
    cube ([17, 17, 4], center=true);
    cube ([14.4, 14.4, 4.2], center=true);
  }
  union () {
    hull () {
      translate ([7.95, 0, 0]) {
        cube ([1, 2.75, 4], center=true);
      }
      translate ([7.2, 0, -1]) {
        rotate (a=90.0, v=[1, 0, 0]) {
          cylinder ($fn=30, h=2.75, r=1, center=true);
        }
      }
    }
    mirror ([0, 1, 0]) {
      mirror ([1, 0, 0]) {
        hull () {
          translate ([7.95, 0, 0]) {
            cube ([1, 2.75, 4], center=true);
          }
          translate ([7.2, 0, -1]) {
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=30, h=2.75, r=1, center=true);
            }
          }
        }
      }
    }
  }
}
