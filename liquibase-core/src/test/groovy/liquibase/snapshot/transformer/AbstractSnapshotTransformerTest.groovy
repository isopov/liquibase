package liquibase.snapshot.transformer

import liquibase.JUnitScope
import liquibase.Scope
import liquibase.snapshot.Snapshot
import liquibase.structure.ObjectReference
import liquibase.structure.core.Column
import liquibase.structure.core.OldDataType
import liquibase.structure.core.Table
import spock.lang.Specification

class AbstractSnapshotTransformerTest extends Specification {

    def "only transforms objects that match the generic interface"() {
        when:
        def transformer = new AbstractSnapshotTransformer<Column>(Column) {

            @Override
            Column transformObject(Column object, Scope scope) {
                object.type = new OldDataType("int");
                return object;
            }
        }

        def snapshot = new Snapshot(JUnitScope.instance)
                .add(new Table(new ObjectReference("table1")))
                .add(new Table(new ObjectReference("table2")))
                .add(new Column(Table, new ObjectReference("table1"), "col1"))
                .add(new Column(Table, new ObjectReference("table1"), "col2"))

        then:
        snapshot.transform(transformer, JUnitScope.instance).get(Column)*.type*.toString() == ["int", "int"]

    }
}