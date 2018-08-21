package de.uni_mannheim.informatik.dws.winter.model.defaultmodel.preprocessing;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;
import de.uni_mannheim.informatik.dws.winter.preprocessing.datatypes.ColumnType;
import de.uni_mannheim.informatik.dws.winter.preprocessing.datatypes.DataType;
import de.uni_mannheim.informatik.dws.winter.preprocessing.datatypes.TypeConverter;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import de.uni_mannheim.informatik.dws.winter.webtables.detectors.TypeDetector;

public class DataSetNormalizer<RecordType extends Record> {
	
	private static final Logger logger = WinterLogManager.getLogger();
	
	private TypeDetector typeDetector;
	
	public DataSetNormalizer(TypeDetector typeDetector){
		this.typeDetector = typeDetector;
	}
	
	public TypeDetector getTypeDetector() {
		return typeDetector;
	}

	public void setTypeDetector(TypeDetector typeDetector) {
		this.typeDetector = typeDetector;
	}

	
	public void detectColumnTypesAndNormalizeDataset(DataSet<RecordType, Attribute> dataSet){
		for(Attribute att: dataSet.getSchema().get()){
			ColumnType columnType = this.detectColumnType(dataSet, att);
			this.normalizeColumn(columnType, dataSet, att);
		}
		logger.info("Type guessing and normalization done!");
	}
	
	public ColumnType detectColumnType(DataSet<RecordType, Attribute> dataSet, Attribute att){
		
			String [] values = new String[dataSet.size()];
			int index = 0;
			for(RecordType record: dataSet.get()){
				values[index] = record.getValue(att);
				index++;
			}
			if(this.typeDetector != null){
				return this.typeDetector.detectTypeForColumn(values, att.getIdentifier());
			}
			else{
				logger.error("No type detector defined!");
				return null;
			}
		
	}
	
	public void normalizeColumn(ColumnType columntype, DataSet<RecordType, Attribute> dataSet, Attribute att){
		TypeConverter tc = new TypeConverter();
		for(RecordType record: dataSet.get()){
			Object value = tc.typeValue(record.getValue(att), columntype.getType(), columntype.getUnit());
			if(value != null){
				record.setValue(att, value.toString()); 
			}
			
		}
		
	}

}
