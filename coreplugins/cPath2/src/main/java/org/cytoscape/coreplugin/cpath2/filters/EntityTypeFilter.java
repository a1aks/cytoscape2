package org.cytoscape.coreplugin.cpath2.filters;

import org.cytoscape.coreplugin.cpath2.util.BioPaxEntityTypeMap;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EntityType Filter.
 *
 * @author Ethan Cerami
 */
public class EntityTypeFilter implements Filter {
    Set<String> entityTypeSet;

    /**
     * Constructor.
     *
     * @param entityTypeSet Set of Entity Types we want to keep.
     */
    public EntityTypeFilter(Set<String> entityTypeSet) {
        this.entityTypeSet = entityTypeSet;
    }

    /**
     * Filters the record list.  Those items which pass the filter
     * are included in the returned list.
     *
     * @param recordList List of RecordType Objects.
     * @return List of RecordType Objects.
     */
    public List<BasicRecordType> filter(List<BasicRecordType> recordList) {
        BioPaxEntityTypeMap bpMap = BioPaxEntityTypeMap.getInstance();
        ArrayList<BasicRecordType> passedList = new ArrayList<BasicRecordType>();
        for (BasicRecordType record : recordList) {
            String type = record.getEntityType();
            if (type != null) {
                if (bpMap.containsKey(type)) {
                    type = (String) bpMap.get(type);
                }
                if (entityTypeSet.contains(type)) {
                    passedList.add(record);
                }
            }
        }
        return passedList;
    }
}