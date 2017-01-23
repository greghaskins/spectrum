package com.greghaskins.spectrum.internal;

import java.util.HashSet;
import java.util.Set;

/**
 * Santises names within a Suite. To stop a name being duplicated or
 * containing characters that upset test runners.
 */
public class NameSanitiser {
  private Set<String> namesUsed = new HashSet<>();

  /**
   * Given a name, deduplicate it and filter out any bad characters.
   * @param name the spec name
   * @return a name which is non-duplicate within this sanitiser and which has known
   *        bad characters removed. Note: this function has side effects - sanitising
   *        a name will cause it to be remembered for future deduplication purposes.
   */
  public String sanitise(final String name) {
    String sanitised = name.replaceAll("\\(", "[")
        .replaceAll("\\)", "]");

    sanitised = sanitised.replaceAll("\\.", "_");

    int suffix = 1;
    String deDuplicated = sanitised;
    while (this.namesUsed.contains(deDuplicated)) {
      deDuplicated = sanitised + "_" + suffix++;
    }
    this.namesUsed.add(deDuplicated);

    return deDuplicated;
  }
}
