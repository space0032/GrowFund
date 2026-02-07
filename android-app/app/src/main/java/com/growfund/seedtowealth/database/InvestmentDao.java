package com.growfund.seedtowealth.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growfund.seedtowealth.model.Investment;

import java.util.List;

@Dao
public interface InvestmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvestment(Investment investment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvestments(List<Investment> investments);

    @Query("SELECT * FROM investments WHERE status = 'ACTIVE'")
    List<Investment> getActiveInvestments();

    @Query("SELECT * FROM investments WHERE id = :investmentId")
    Investment getInvestmentById(Long investmentId);

    @Query("DELETE FROM investments WHERE status = 'MATURED'")
    void deleteMaturedInvestments();

    @Query("DELETE FROM investments")
    void deleteAll();

    @Query("DELETE FROM investments WHERE id IN (:investmentIds)")
    void deleteInvestmentsByIds(List<Long> investmentIds);

    @Query("UPDATE investments SET status = :newStatus WHERE id IN (:investmentIds)")
    void updateInvestmentStatus(List<Long> investmentIds, String newStatus);
}
