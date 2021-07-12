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
import cc.ab.base.widget.fittext.emoji.category.*;
import cc.ab.base.widget.fittext.emoji.core.EmojiCategory;

public final class GoogleEmojiProvider {
  @NonNull public EmojiCategory[] getCategories() {
    return new EmojiCategory[] {
        new SmileysAndPeopleCategory(),
        new AnimalsAndNatureCategory(),
        new FoodAndDrinkCategory(),
        new ActivitiesCategory(),
        new TravelAndPlacesCategory(),
        new ObjectsCategory(),
        new SymbolsCategory(),
        new FlagsCategory()
    };
  }
}