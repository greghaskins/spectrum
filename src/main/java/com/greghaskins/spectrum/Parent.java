package com.greghaskins.spectrum;

interface Parent {

  void focus(Child child);

  boolean isIgnored();

  Parent NONE = new Parent() {
    @Override
    public void focus(Child child) { }

    @Override
    public boolean isIgnored() {
      return false;
    }
  };
}
