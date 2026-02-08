package com.growfund.seedtowealth.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "farm_equipment")
public class FarmEquipment {

    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "farm_id")
    private Long farmId;

    @ColumnInfo(name = "equipment_id")
    private Long equipmentId;

    @ColumnInfo(name = "purchase_date")
    private String purchaseDate;

    @ColumnInfo(name = "durability_remaining")
    private Integer durabilityRemaining;

    private Boolean active;

    @ColumnInfo(name = "last_used")
    private String lastUsed;

    // Transient fields (not stored in database)
    @Ignore
    private Equipment equipment; // For joined queries

    // Constructors
    public FarmEquipment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getDurabilityRemaining() {
        return durabilityRemaining;
    }

    public void setDurabilityRemaining(Integer durabilityRemaining) {
        this.durabilityRemaining = durabilityRemaining;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    // Helper methods
    public boolean isUsable() {
        return active != null && active && durabilityRemaining != null && durabilityRemaining > 0;
    }

    public int getDurabilityPercentage() {
        if (equipment == null || equipment.getMaxDurability() == null || equipment.getMaxDurability() == 0) {
            return 0;
        }
        if (durabilityRemaining == null) {
            return 0;
        }
        return (int) ((durabilityRemaining * 100.0) / equipment.getMaxDurability());
    }

    public String getDurabilityText() {
        if (durabilityRemaining == null) {
            return "Unknown";
        }
        return durabilityRemaining + " uses left";
    }
}
