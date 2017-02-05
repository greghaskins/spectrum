package com.greghaskins.spectrum.internal;

public interface Parent {

  void focus(Child child);

  boolean isIgnored();

  Hooks getInheritableHooks();

  Parent NONE = new Parent() {
    @Override
    public void focus(final Child child) {}

    @Override
    public boolean isIgnored() {
      return false;
    }

    @Override
    public Hooks getInheritableHooks() {
      return new Hooks();
    }
  };
}
