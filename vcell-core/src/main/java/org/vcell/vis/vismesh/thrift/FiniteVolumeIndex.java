/**
 * Autogenerated by Thrift Compiler (0.18.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.vcell.vis.vismesh.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.18.1)")
public class FiniteVolumeIndex implements org.apache.thrift.TBase<FiniteVolumeIndex, FiniteVolumeIndex._Fields>, java.io.Serializable, Cloneable, Comparable<FiniteVolumeIndex> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FiniteVolumeIndex");

  private static final org.apache.thrift.protocol.TField GLOBAL_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("globalIndex", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField REGION_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("regionIndex", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FiniteVolumeIndexStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FiniteVolumeIndexTupleSchemeFactory();

  public int globalIndex; // required
  public int regionIndex; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    GLOBAL_INDEX((short)1, "globalIndex"),
    REGION_INDEX((short)2, "regionIndex");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // GLOBAL_INDEX
          return GLOBAL_INDEX;
        case 2: // REGION_INDEX
          return REGION_INDEX;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    @Override
    public short getThriftFieldId() {
      return _thriftId;
    }

    @Override
    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __GLOBALINDEX_ISSET_ID = 0;
  private static final int __REGIONINDEX_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.GLOBAL_INDEX, new org.apache.thrift.meta_data.FieldMetaData("globalIndex", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.REGION_INDEX, new org.apache.thrift.meta_data.FieldMetaData("regionIndex", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FiniteVolumeIndex.class, metaDataMap);
  }

  public FiniteVolumeIndex() {
  }

  public FiniteVolumeIndex(
    int globalIndex,
    int regionIndex)
  {
    this();
    this.globalIndex = globalIndex;
    setGlobalIndexIsSet(true);
    this.regionIndex = regionIndex;
    setRegionIndexIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FiniteVolumeIndex(FiniteVolumeIndex other) {
    __isset_bitfield = other.__isset_bitfield;
    this.globalIndex = other.globalIndex;
    this.regionIndex = other.regionIndex;
  }

  @Override
  public FiniteVolumeIndex deepCopy() {
    return new FiniteVolumeIndex(this);
  }

  @Override
  public void clear() {
    setGlobalIndexIsSet(false);
    this.globalIndex = 0;
    setRegionIndexIsSet(false);
    this.regionIndex = 0;
  }

  public int getGlobalIndex() {
    return this.globalIndex;
  }

  public FiniteVolumeIndex setGlobalIndex(int globalIndex) {
    this.globalIndex = globalIndex;
    setGlobalIndexIsSet(true);
    return this;
  }

  public void unsetGlobalIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __GLOBALINDEX_ISSET_ID);
  }

  /** Returns true if field globalIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetGlobalIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __GLOBALINDEX_ISSET_ID);
  }

  public void setGlobalIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __GLOBALINDEX_ISSET_ID, value);
  }

  public int getRegionIndex() {
    return this.regionIndex;
  }

  public FiniteVolumeIndex setRegionIndex(int regionIndex) {
    this.regionIndex = regionIndex;
    setRegionIndexIsSet(true);
    return this;
  }

  public void unsetRegionIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __REGIONINDEX_ISSET_ID);
  }

  /** Returns true if field regionIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetRegionIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __REGIONINDEX_ISSET_ID);
  }

  public void setRegionIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __REGIONINDEX_ISSET_ID, value);
  }

  @Override
  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case GLOBAL_INDEX:
      if (value == null) {
        unsetGlobalIndex();
      } else {
        setGlobalIndex((java.lang.Integer)value);
      }
      break;

    case REGION_INDEX:
      if (value == null) {
        unsetRegionIndex();
      } else {
        setRegionIndex((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case GLOBAL_INDEX:
      return getGlobalIndex();

    case REGION_INDEX:
      return getRegionIndex();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  @Override
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case GLOBAL_INDEX:
      return isSetGlobalIndex();
    case REGION_INDEX:
      return isSetRegionIndex();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof FiniteVolumeIndex)
      return this.equals((FiniteVolumeIndex)that);
    return false;
  }

  public boolean equals(FiniteVolumeIndex that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_globalIndex = true;
    boolean that_present_globalIndex = true;
    if (this_present_globalIndex || that_present_globalIndex) {
      if (!(this_present_globalIndex && that_present_globalIndex))
        return false;
      if (this.globalIndex != that.globalIndex)
        return false;
    }

    boolean this_present_regionIndex = true;
    boolean that_present_regionIndex = true;
    if (this_present_regionIndex || that_present_regionIndex) {
      if (!(this_present_regionIndex && that_present_regionIndex))
        return false;
      if (this.regionIndex != that.regionIndex)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + globalIndex;

    hashCode = hashCode * 8191 + regionIndex;

    return hashCode;
  }

  @Override
  public int compareTo(FiniteVolumeIndex other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetGlobalIndex(), other.isSetGlobalIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGlobalIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.globalIndex, other.globalIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetRegionIndex(), other.isSetRegionIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRegionIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.regionIndex, other.regionIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  @Override
  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  @Override
  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FiniteVolumeIndex(");
    boolean first = true;

    sb.append("globalIndex:");
    sb.append(this.globalIndex);
    first = false;
    if (!first) sb.append(", ");
    sb.append("regionIndex:");
    sb.append(this.regionIndex);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'globalIndex' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'regionIndex' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FiniteVolumeIndexStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public FiniteVolumeIndexStandardScheme getScheme() {
      return new FiniteVolumeIndexStandardScheme();
    }
  }

  private static class FiniteVolumeIndexStandardScheme extends org.apache.thrift.scheme.StandardScheme<FiniteVolumeIndex> {

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot, FiniteVolumeIndex struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // GLOBAL_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.globalIndex = iprot.readI32();
              struct.setGlobalIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // REGION_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.regionIndex = iprot.readI32();
              struct.setRegionIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetGlobalIndex()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'globalIndex' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetRegionIndex()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'regionIndex' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot, FiniteVolumeIndex struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(GLOBAL_INDEX_FIELD_DESC);
      oprot.writeI32(struct.globalIndex);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(REGION_INDEX_FIELD_DESC);
      oprot.writeI32(struct.regionIndex);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FiniteVolumeIndexTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public FiniteVolumeIndexTupleScheme getScheme() {
      return new FiniteVolumeIndexTupleScheme();
    }
  }

  private static class FiniteVolumeIndexTupleScheme extends org.apache.thrift.scheme.TupleScheme<FiniteVolumeIndex> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FiniteVolumeIndex struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.globalIndex);
      oprot.writeI32(struct.regionIndex);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FiniteVolumeIndex struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.globalIndex = iprot.readI32();
      struct.setGlobalIndexIsSet(true);
      struct.regionIndex = iprot.readI32();
      struct.setRegionIndexIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

