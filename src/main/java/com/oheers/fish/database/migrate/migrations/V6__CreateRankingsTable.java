package com.oheers.fish.database.migrate.migrations;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;

/**
 * @author sarhatabaot
 */
public class V6__CreateRankingsTable extends BaseJavaMigration {
    @Override
    public void migrate(@NotNull Context context) throws Exception {
        final String largestFishRankings = "CREATE TABLE emf_rankings_largest_fish (" +
            "uuid VARCHAR(128) NOT NULL, " +
            "fish_name VARCHAR(256) NOT NULL, " +
            "fish_rarity VARCHAR(256) NOT NULL, " +
            "fish_length DOUBLE NOT NULL";
        final String largestTotalRankings = "CREATE TABLE emf_rankings_largest_total";
        final String mostFishRankings = "CREATE TABLE emf_rankings_most_fish";
        try(PreparedStatement statement = context.getConnection().prepareStatement(largestFishRankings)) {
            statement.execute();
        }
        try(PreparedStatement statement = context.getConnection().prepareStatement(largestTotalRankings)) {
            statement.execute();
        }
        try(PreparedStatement statement = context.getConnection().prepareStatement(mostFishRankings)) {
            statement.execute();
        }
        
    }
}
