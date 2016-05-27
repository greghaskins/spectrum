package specs;

import com.greghaskins.spectrum.Spectrum;
import helpers.SpectrumRunner;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Spectrum.class)
public class IgnoredSpecs {
    {
       describe("Ignored specs", () -> {
           it("are declared with `xit`", () -> {
               final Result result = SpectrumRunner.run(getSuiteWithIgnoredSpecs());
               assertThat(result.getFailureCount(), is(0));
           });

           it("ignores tests that are xit", () -> {
               final Result result = SpectrumRunner.run(getSuiteWithIgnoredSpecs());
               assertThat(result.getRunCount(), is(1));
               assertThat(result.getIgnoreCount(), is(2));
           });

           describe("with nesting", () -> {
               it("ignores only the nested spec", () -> {
                   final Result result = SpectrumRunner.run(getSuiteWithNestedIgnoredSpecs());
                   assertThat(result.getFailureCount(), is(0));
                   assertThat(result.getRunCount(), is(1));
                   assertThat(result.getIgnoreCount(), is(1));
               });
           });
       });

        describe("Ignored suites", () -> {
            it("are declared with `xdescribe`", () -> {
                final Result result = SpectrumRunner.run(getSuiteWithIgnoredSubSuites());
                assertThat(result.getFailureCount(), is(0));
            });

            it("ignores tests that are xdescribe", () -> {
                final Result result = SpectrumRunner.run(getSuiteWithIgnoredSubSuites());
                assertThat(result.getRunCount(), is(1));
                assertThat(result.getIgnoreCount(), is(4));
            });

            describe("with nesting", () -> {
                it("cause specs in other suites to also be ignored", () -> {
                    final Result result = SpectrumRunner.run(getSuiteWithNestedIgnoredSuites());
                    assertThat(result.getFailureCount(), is(0));
                    assertThat(result.getRunCount(), is(1));
                    assertThat(result.getIgnoreCount(), is(1));
                });

                describe("and the nested suite has a focus", () -> {
                   it("ignores the focus", () -> {
                       final Result result = SpectrumRunner.run(getSuiteWithNestedIgnoredSuitesAndFocusedSpecs());
                       assertThat(result.getFailureCount(), is(0));
                       assertThat(result.getRunCount(), is(1));
                       assertThat(result.getIgnoreCount(), is(1));
                   });
                });
            });
        });

        describe("Ignored specs example", () -> {
            final Value<Result> result = value();

            beforeEach(() -> {
                result.value = SpectrumRunner.run(getIgnoredSpecsExample());
            });

            it("has three ignored specs", () -> {
                assertThat(result.value.getIgnoreCount(), is(4));
            });

            it("does not run unfocused specs", () -> {
                assertThat(result.value.getFailureCount(), is(0));
            });
        });
    }

    private static Class<?> getSuiteWithIgnoredSpecs() {
        class Suite {
            {

                describe("A spec that", () -> {

                    it("is not ignored and will run", () -> {
                        assertThat(true, is(true));
                    });

                    xit("is ignored and will not run", () -> {
                        assertThat(true, is(false));
                    });

                    xit("is ignored and has no block");
                });
            }
        }

        return Suite.class;
    }

    private static Class<?> getSuiteWithNestedIgnoredSpecs() {
        class Suite {
            {

                it("should run because it isn't ignored", () -> {
                    assertThat(true, is(true));
                });

                describe("a nested context", () -> {
                    xit("is ignored and will not run", () -> {
                        assertThat(true, is(false));
                    });
                });
            }
        }

        return Suite.class;
    }

    private static Class<?> getSuiteWithIgnoredSubSuites() {
        class Suite {
            {
                describe("an un-ignored suite", () -> {
                    it("is not ignored", () -> {
                        assertThat(true, is(true));
                    });
                });

                xdescribe("ignored describe", () -> {
                    it("will not run", () -> {
                        assertThat(true, is(false));
                    });

                    it("will also not run", () -> {
                        assertThat(true, is(false));
                    });

                    it("will also not run a focused test", () -> {
                        assertThat(true, is(false));
                    });
                });

                xdescribe("ignored describe with no block");
            }
        }

        return Suite.class;
    }

    private static Class<?> getSuiteWithNestedIgnoredSuitesAndFocusedSpecs() {
        class Suite {
            {

                describe("a nested context", () -> {
                    describe("with a sub-suite", () -> {
                        it("will run despite having a focused test", () -> {
                            assertThat(true, is(true));
                        });
                    });
                });

                xdescribe("a nested ignored context", () -> {
                    describe("with a sub-suite", () -> {
                        fit("will not run forcued test", () -> {
                            assertThat(true, is(false));
                        });
                    });
                });
            }
        }

        return Suite.class;
    }

    private static Class<?> getSuiteWithNestedIgnoredSuites() {
        class Suite {
            {

                describe("a nested context", () -> {
                    describe("with a sub-suite", () -> {
                        it("will run", () -> {
                            assertThat(true, is(true));
                        });
                    });
                });

                xdescribe("a nested ignored context", () -> {
                    describe("with a sub-suite", () -> {
                        it("will not run", () -> {
                            assertThat(true, is(false));
                        });
                    });
                });
            }
        }

        return Suite.class;
    }

    private static Class<?> getIgnoredSpecsExample() {
        class FocusedSpecsExample {
            {
                describe("Ignored specs", () -> {

                    xit("is ignored and will not run", () -> {
                        throw new Exception();
                    });

                    it("is not ignored and will run", () -> {
                        assertThat(true, is(true));
                    });

                    xdescribe("an ignored suite", () -> {

                        it("will not run", () -> {
                            throw new Exception();
                        });

                        describe("with nesting", () -> {
                            it("all its specs", () -> {
                                throw new Exception();
                            });

                            fit("including focused specs", () -> {
                                throw new Exception();
                            });
                        });
                    });
                });
            }
        }

        return FocusedSpecsExample.class;
    }
}
