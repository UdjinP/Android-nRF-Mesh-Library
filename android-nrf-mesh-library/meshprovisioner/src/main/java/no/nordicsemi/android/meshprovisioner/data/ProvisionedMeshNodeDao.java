package no.nordicsemi.android.meshprovisioner.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.RestrictTo;

import java.util.List;

import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;

@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("unused")
@Dao
public interface ProvisionedMeshNodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final ProvisionedMeshNode provisionedMeshNode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final List<ProvisionedMeshNode> provisionedMeshNode);

    @Update
    void update(final ProvisionedMeshNode meshNode);

    @Delete
    void delete(final ProvisionedMeshNode meshNode);

    @Query("DELETE from nodes")
    void deleteAll();

}