package com.greghaskins.spectrum;

interface Parent {

  void focus(Child child);

  static final Parent NONE = new Parent() {

    @Override
    public void focus(final Child child) {
    }
  };

}
