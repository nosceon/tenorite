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
package net.tenorite.game.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModes;
import net.tenorite.game.GameRepository;
import net.tenorite.game.actors.GamesActor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ServiceLoader;

/**
 * @author Johan Siebens
 */
@Configuration
public class GameConfig {

    @Bean
    public ActorRef gameActor(ActorSystem system, GameRepository gameRepository) {
        return system.actorOf(GamesActor.props(gameRepository));
    }

    @Bean
    public GameModes gameModes() {
        return new GameModes(ServiceLoader.load(GameMode.class));
    }

}
