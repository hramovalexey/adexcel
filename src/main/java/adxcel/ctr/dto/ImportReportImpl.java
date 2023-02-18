package adxcel.ctr.dto;

import adxcel.ctr.service.dataimport.TableType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportReportImpl implements ImportReport {
    @NonNull
    private TableType tableType;
    int sourceRowsProcessed;
    private int rowsImported;
    private List<String> uidCollisions = new ArrayList<>();
    private List<String> unresolvedUidCollisions = new ArrayList<>();
    private List<String> unmappedUids = new ArrayList<>();

    public int incrementAndGetImportedRows() {
        return ++rowsImported;
    }

    public int incrementAndGetSourceRows() {
        return ++sourceRowsProcessed;
    }

    @Override
    public String toString() {
        return String.format("tableType=%s, sourceRowsProcessed=%d rowsImported=%d, uidCollisionsQty=%d, unresolvedUidCollisionsQty=%d, unmappedUidQty=%d, unresolvedList=%s",
                tableType.toString(), sourceRowsProcessed, rowsImported, uidCollisions.size(), unresolvedUidCollisions.size(), unmappedUids.size(), unresolvedUidCollisions.toString());
    }
}
