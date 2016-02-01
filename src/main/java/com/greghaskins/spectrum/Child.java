package com.greghaskins.spectrum;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

interface Child  {

    public Description getDescription();

    public void run(RunNotifier notifier);

    public int testCount();

}
