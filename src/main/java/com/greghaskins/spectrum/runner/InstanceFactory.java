package com.greghaskins.spectrum.runner;

interface InstanceFactory<T, TOuter> {

    T makeInstance(final TOuter outerInstance);

}
