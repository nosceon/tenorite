package net.tenorite.badges.validators;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class GameWonWithHitchhikersGuideTest extends AbstractValidatorTestCase {

    private GameWonWithHitchhikersGuide validator = new GameWonWithHitchhikersGuide(BADGE, asList(PlayingStats::getNrOfBlocks, PlayingStats::getNrOfLines));

    @Test
    public void testEarnBadge() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .nrOfBlocks(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null), b -> b
            .nrOfBlocks(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null), b -> b
            .nrOfBlocks(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).extracting("upgrade").containsExactly(false);
        assertThat(published).extracting("badge.name").containsExactly("john");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(1L);

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(1);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of("john", BADGE, 1000, 13, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 13);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .nrOfLines(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null), b -> b
            .nrOfLines(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null), b -> b
            .nrOfLines(HitchhikersGuide.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE)
        );

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).extracting("upgrade").containsExactly(true);
        assertThat(published).extracting("badge.name").containsExactly("john");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(14L);

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(14);
    }

}