package adxcel.ctr.dto;

import adxcel.ctr.service.dataimport.TableType;

import java.util.List;

// TODO import report can be transferred to client when online data uploading will be implemented
public interface ImportReport {
    TableType getTableType();
    int getSourceRowsProcessed();
    int getRowsImported();
    List<String> getUidCollisions();
    List<String> getUnresolvedUidCollisions();
    List<String> getUnmappedUids();
    int incrementAndGetImportedRows();
    int incrementAndGetSourceRows();
}
