package com.greghaskins.spectrum.internal;

import com.greghaskins.spectrum.model.Hooks;

public interface Parent {

  void focus(Child child);

  boolean isIgnored();

  Hooks getInheritedHooks();

  Parent NONE = new Parent() {
    @Override
    public void focus(final Child child) {}

    @Override
    public boolean isIgnored() {
      return false;
    }

    @Override
    public Hooks getInheritedHooks() {
      return new Hooks();
    }
  };
}
