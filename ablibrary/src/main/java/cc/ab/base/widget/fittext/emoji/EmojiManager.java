/*
 * Copyright (C) 2016 - Niklas Baudy, Ruben Gees, Mario Đanić and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.ab.base.widget.fittext.emoji;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cc.ab.base.widget.fittext.emoji.core.Emoji;
import cc.ab.base.widget.fittext.emoji.core.EmojiCategory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EmojiManager where an EmojiProvider can be installed for further usage.
 */
@SuppressWarnings("PMD.ForLoopCanBeForeach") public final class EmojiManager {
  private static final EmojiManager INSTANCE = new EmojiManager();
  private static final int GUESSED_UNICODE_AMOUNT = 3000;
  private static final int GUESSED_TOTAL_PATTERN_LENGTH = GUESSED_UNICODE_AMOUNT * 4;

  private static final Comparator<String> STRING_LENGTH_COMPARATOR = (first, second) -> {
    final int firstLength = first.length();
    final int secondLength = second.length();

    return Integer.compare(secondLength, firstLength);
  };

  private final Map<String, Emoji> emojiMap = new LinkedHashMap<>(GUESSED_UNICODE_AMOUNT);
  private EmojiCategory[] categories;
  private Pattern emojiPattern;

  private EmojiManager() {
    // No instances apart from singleton.
  }

  public static EmojiManager getInstance() {
    synchronized (EmojiManager.class) {
      return INSTANCE;
    }
  }

  static {
    install(new GoogleEmojiProvider());
  }

  /**
   * Installs the given EmojiProvider.
   *
   * NOTE: That only one can be present at any time.
   *
   * @param provider the provider that should be installed.
   */
  public static void install(@NonNull final GoogleEmojiProvider provider) {
    synchronized (EmojiManager.class) {
      INSTANCE.categories = checkNotNull(provider.getCategories(), "categories == null");
      INSTANCE.emojiMap.clear();

      final List<String> unicodesForPattern = new ArrayList<>(GUESSED_UNICODE_AMOUNT);

      final int categoriesSize = INSTANCE.categories.length;
      //noinspection
      for (int i = 0; i < categoriesSize; i++) {
        final Emoji[] emojis = checkNotNull(INSTANCE.categories[i].getEmojis(), "emojies == null");

        final int emojisSize = emojis.length;
        //noinspection ForLoopReplaceableByForEach
        for (int j = 0; j < emojisSize; j++) {
          final Emoji emoji = emojis[j];
          final String unicode = emoji.getUnicode();
          final List<Emoji> variants = emoji.getVariants();

          INSTANCE.emojiMap.put(unicode, emoji);
          unicodesForPattern.add(unicode);

          //noinspection
          for (int k = 0; k < variants.size(); k++) {
            final Emoji variant = variants.get(k);
            final String variantUnicode = variant.getUnicode();

            INSTANCE.emojiMap.put(variantUnicode, variant);
            unicodesForPattern.add(variantUnicode);
          }
        }
      }

      if (unicodesForPattern.isEmpty()) {
        throw new IllegalArgumentException("Your EmojiProvider must at least have one category with at least one emoji.");
      }

      // We need to sort the unicodes by length so the longest one gets matched first.
      Collections.sort(unicodesForPattern, STRING_LENGTH_COMPARATOR);

      final StringBuilder patternBuilder = new StringBuilder(GUESSED_TOTAL_PATTERN_LENGTH);

      final int unicodesForPatternSize = unicodesForPattern.size();
      for (int i = 0; i < unicodesForPatternSize; i++) {
        patternBuilder.append(Pattern.quote(unicodesForPattern.get(i))).append('|');
      }

      final String regex = patternBuilder.deleteCharAt(patternBuilder.length() - 1).toString();
      INSTANCE.emojiPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
  }

  /**
   * Destroys the EmojiManager. This means that all internal data structures are released as well as
   * all data associated with installed {@link Emoji}s. For the existing {@link GoogleEmojiProvider}s this
   * means the memory-heavy emoji sheet.
   *
   * @see #destroy()
   */
  public static void destroy() {
    synchronized (EmojiManager.class) {
      release();
      INSTANCE.emojiMap.clear();
      INSTANCE.categories = null;
      INSTANCE.emojiPattern = null;
    }
  }

  /**
   * Releases all data associated with installed {@link Emoji}s. For the existing {@link GoogleEmojiProvider}s this
   * means the memory-heavy emoji sheet.
   *
   * In contrast to {@link #destroy()}, this does <b>not</b> destroy the internal
   * data structures and thus, you do not need to {@link #install(GoogleEmojiProvider)} again before using the EmojiManager.
   *
   * @see #destroy()
   */
  public static void release() {
    synchronized (EmojiManager.class) {
      for (final Emoji emoji : INSTANCE.emojiMap.values()) {
        emoji.destroy();
      }
    }
  }

  @NonNull
  public List<EmojiRange> findAllEmojis(@Nullable final CharSequence text) {
    verifyInstalled();

    final List<EmojiRange> result = new ArrayList<>();

    if (text != null && text.length() > 0) {
      final Matcher matcher = emojiPattern.matcher(text);

      while (matcher.find()) {
        final Emoji found = findEmoji(text.subSequence(matcher.start(), matcher.end()));

        if (found != null) {
          result.add(new EmojiRange(matcher.start(), matcher.end(), found));
        }
      }
    }

    return result;
  }

  private @Nullable Emoji findEmoji(@NonNull final CharSequence candidate) {
    verifyInstalled();

    // We need to call toString on the candidate, since the emojiMap may not find the requested entry otherwise, because the type is different.
    return emojiMap.get(candidate.toString());
  }

  private void verifyInstalled() {
    if (categories == null) {
      throw new IllegalStateException("Please install an EmojiProvider through the EmojiManager.install() method first.");
    }
  }

  private static @NonNull <T> T checkNotNull(@Nullable final T reference, final String message) {
    if (reference == null) {
      throw new IllegalArgumentException(message);
    }
    return reference;
  }
}