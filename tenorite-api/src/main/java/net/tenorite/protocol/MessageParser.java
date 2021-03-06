/*
 * Copyright 2016 the original author or authors.
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
 */
package net.tenorite.protocol;

import net.tenorite.core.Special;

import java.util.Objects;
import java.util.Optional;

import static java.lang.Integer.valueOf;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Johan Siebens
 */
public class MessageParser {

    public static Optional<Message> parse(String raw) {
        return parse(raw, false);
    }

    public static Optional<Message> parse(String raw, boolean serverMessage) {
        String[] split = raw.trim().split("\\s+");
        switch (split[0]) {
            case "pline":
                return of(PlineMessage.of(valueOf(split[1]), raw.substring("pline".length() + 3).trim()));
            case "plineact":
                return of(PlineActMessage.of(valueOf(split[1]), raw.substring("plineact".length() + 3).trim()));
            case "gmsg":
                return of(GmsgMessage.of(raw.substring("gmsg".length() + 1).trim()));
            case "team":
                return of(TeamMessage.of(valueOf(split[1]), raw.substring("team".length() + 3).trim()));
            case "lvl":
                return of(LvlMessage.of(valueOf(split[1]), valueOf(split[2])));
            case "f":
                return of(FieldMessage.of(valueOf(split[1]), raw.substring("f".length() + 3).trim(), serverMessage));
            case "sb":
                Integer target = valueOf(split[1]);
                Integer sender = valueOf(split[3]);
                String special = split[2];
                if (special.length() == 1) {
                    return Special.valueOf(special.charAt(0)).map(s -> SpecialBlockMessage.of(sender, s, target, serverMessage));
                }
                else if (Objects.equals(special, "cs1") || Objects.equals(special, "cs2") || Objects.equals(special, "cs4")) {
                    int lines = Integer.valueOf(special.substring(2));
                    return of(ClassicStyleAddMessage.of(sender, lines));
                }
                else {
                    return empty();
                }
            case "startgame":
                return valueOf(split[1]) == 0 ? of(StopGameMessage.of(valueOf(split[2]))) : of(StartGameMessage.of(valueOf(split[2])));
            case "pause":
                return valueOf(split[1]) == 0 ? of(ResumeGameMessage.of(valueOf(split[2]))) : of(PauseGameMessage.of(valueOf(split[2])));
            case "playerlost":
                return of(PlayerLostMessage.of(valueOf(split[1])));
            case "playerwon":
                return of(PlayerWonMessage.of(valueOf(split[1])));
            case "playerleave":
                return of(PlayerLeaveMessage.of(valueOf(split[1])));
            default:
                return empty();
        }
    }

}
