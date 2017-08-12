package ucar.units_vcell;
import java.io.ByteArrayInputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.LineNumberReader;
    import java.io.Serializable;
    import java.text.DateFormat;
    import java.text.DecimalFormat;
    import java.text.NumberFormat;
    import java.text.SimpleDateFormat;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.Comparator;
    import java.util.Date;
    import java.util.Locale;
    import java.util.StringTokenizer;
import java.util.TimeZone;


        /**
     * Standard formatter/parser for unit specifications.
     *
     * @author Steven R. Emmerson
     */
    public final class
    StandardUnitFormat
        extends UnitFormatImpl implements StandardUnitFormatConstants {
        /**
	 * The singleton instance of this class.
	 * @serial
	 */
        private static StandardUnitFormat       _instance;

        /**
	 * The Julian day number of the (artificial) time origin.
	 * @serial
	 */
        private static final long               julianDayOrigin =
            StandardUnitFormat.julianDay(2001, 1, 1);

        /**
	 * The date formatter.
	 * @serial
	 */
        private static final SimpleDateFormat   dateFormat;

        /**
	 * The Comparator for ordering base units for printing.  Orders
	 * Factor-s by decreasing exponent (major) and lexically (minor).
	 * @serial
	 */
        private static final Comparator         factorComparator =
            new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    Factor      f1 = ((Factor)o1);
                    Factor      f2 = ((Factor)o2);
                    RationalNumber comp = f2.getExponent().sub(f1.getExponent());
                    if (comp.isZero()){
                        return f1.getID().compareTo(f2.getID());
                    }else{
	                    return (int)(100.0*comp.doubleValue());
                    }
                }
            };

        static
        {
            dateFormat =
                (SimpleDateFormat)DateFormat.getDateInstance(
                    DateFormat.SHORT, Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormat.applyPattern(" '@' yyyy-MM-dd HH:mm:ss.SSS 'UTC'");
        }

  public StandardUnitFormatTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[24];
  final private int[] jj_la1_0 = {0x2840090,0x800,0x2804090,0x3805190,0x1800100,0x3805190,0x1800100,0x2004000,0x8000000,0x8000010,0x2804090,0x10,0x800000,0x800090,0x2800090,0x801090,0x801090,0x100,0x4000,0x400010,0x100,0x404010,0x404110,0x404110,};

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

            /**
	 * Constructs from nothing.
	 */
        private
        StandardUnitFormat()
        {
            this(new ByteArrayInputStream("".getBytes()));
        }


  public StandardUnitFormat(java.io.InputStream stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new StandardUnitFormatTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


  public StandardUnitFormat(java.io.Reader stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new StandardUnitFormatTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


  public StandardUnitFormat(StandardUnitFormatTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


        private boolean
        contains(StringBuffer buf, int start, char[] chars)
        {
            int n = buf.length();
            for (int i = start; i < n; i++)
            {
                char    c = buf.charAt(i);
                for (int j = 0; j < chars.length; j++)
                {
                    if (c == chars[j])
                        return true;
                }
            }
            return false;
        }


  final public void disable_tracing() {
  }


  final public void enable_tracing() {
  }


        private StringBuffer
        format(BaseUnit baseUnit, StringBuffer buf)
        {
            return buf.append(baseUnit.getSymbol());
        }


        private StringBuffer
        format(DerivedUnit unit, StringBuffer buf)
        {
            Factor[]    factors = unit.getDimension().getFactors();
            Arrays.sort(factors, factorComparator);
            for (int i = 0; i < factors.length; i++)
                format(factors[i], buf).append('.');
            if (factors.length != 0)
                buf.setLength(buf.length()-1);
            return buf;
        }


        /**
	 * Formats a Factor.
	 * @param factor	The factor to be formatted.
	 * @param buf		The buffer to append to.
	 * @return		The appended-to buffer.
	 */
        public StringBuffer
        format(Factor factor, StringBuffer buf)
        {
            return buf.append(factor.toString());
        }


        private StringBuffer
        format(OffsetUnit unit, StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            double      offset = unit.getOffset();
            if (offset == 0.0)
            {
                format(unit.getUnit(), buf, normalize);
            }
            else
            {
                int     start = buf.length();
                format(unit.getUnit(), buf, normalize);
                return (isBlackSpace(buf, start)
                            ? buf
                            : buf.insert(start, '(').append(')')).
                        append(" @ ").append(offset);
            }
            return buf;
        }


        private StringBuffer
        format(ScaledUnit unit, StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            String      result;
            double      scale = unit.getScale();
            if (scale != 0.0)
            {
                if (scale == 1)
                {
                    format(unit.getUnit(), buf, normalize);
                }
                else
                {
                    buf.append(scale).append(' ');
                    int start = buf.length();
                    format(unit.getUnit(), buf, normalize);
                    if (start == buf.length())
                        buf.setLength(start-1);
                }
            }
            return buf;
        }


        private StringBuffer
        format(TimeScaleUnit unit, StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            return format(unit.getUnit(), buf, normalize).
                append(dateFormat.format(unit.getOrigin()));
        }


        /**
	 * Formats a unit.  The symbol or name will be used if available;
	 * otherwise, a specification in terms of underlying units will be
	 * returned.
	 * @param unit		The unit to be formatted.
	 * @param buf		The buffer to append to.
	 * @return		The appended-to buffer.
	 * @throws UnitClassException	The class of the unit is unknown.
	 */
        public StringBuffer
        format(Unit unit, StringBuffer buf)
            throws UnitClassException
        {
            return format(unit, buf, true);
        }


        /**
	 * Formats a unit.
	 * @param unit		The unit to be formatted.
	 * @param buf		The buffer to append to.
	 * @param normalize	Whether or not to reduce the unit.
	 * @return		The appended-to buffer.
	 * @throws UnitClassException	The class of the unit is unknown.
	 */
        private StringBuffer
        format(Unit unit, StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            boolean     done = false;
            if (!normalize)
            {
                String  id = unit.getSymbol();
                if (id == null)
                    id = unit.getName();
                if (id != null)
                {
                    buf.append(id.replace(' ', '_'));
                    done = true;
                }
            }
            if (!done)
            {
                if (unit instanceof BaseUnit)
                    format((BaseUnit)unit, buf);
                else
                if (unit instanceof DerivedUnit)
                    format((DerivedUnit)unit, buf);
                else
                if (unit instanceof ScaledUnit)
                    format((ScaledUnit)unit, buf, normalize);
                else
                if (unit instanceof OffsetUnit)
                    format((OffsetUnit)unit, buf, normalize);
                else
                if (unit instanceof TimeScaleUnit)
                    format((TimeScaleUnit)unit, buf, normalize);
                else
                    throw new UnitClassException(unit);
            }
            return buf;
        }


        private StringBuffer
        formatBinaryUnit(Unit first, Unit second, char separator,
                StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            int start = buf.length();
            group(first, buf, normalize);
            if (start < buf.length())
                buf.append(separator);
            group(second, buf, normalize);
            if (buf.charAt(buf.length()-1) == separator)
                buf.setLength(buf.length()-1);
            return buf;
        }


  final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[28];
    for (int i = 0; i < 28; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 24; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 28; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }


  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }


        /**
	 * Gets a prefix from the prefix database.
	 */
        private static Prefix
        getPrefix(String string)
            throws PrefixDBException
        {
            PrefixDB    prefixDB = PrefixDBManager.instance();
            Prefix      prefix = prefixDB.getPrefixByName(string);
            if (prefix == null)
                prefix = prefixDB.getPrefixBySymbol(string);
            return prefix;
        }


  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }


        /**
	 * Gets a unit from a unit database.
	 */
        private static Unit
        getUnit(UnitDB unitDB, String string)
            throws UnitDBAccessException
        {
            return unitDB.get(string);
        }


        private StringBuffer
        group(Unit unit, StringBuffer buf, boolean normalize)
            throws UnitClassException
        {
            int start = buf.length();
            format(unit, buf, normalize);
            if (!isBlackSpace(buf, start))
            {
                buf.insert(start, '(');
                buf.append(')');
            }
            return buf;
        }


        /**
	 * Returns an instance of this class.
	 * @return		An instance of this class.
	 */
        public static StandardUnitFormat
        instance()
        {
            if (_instance == null)
            {
                synchronized(StandardUnitFormat.class)
                {
                    if (_instance == null)
                        _instance = new StandardUnitFormat();
                }
            }
            return _instance;
        }


        private static boolean
        isBlackSpace(StringBuffer buf, int start)
        {
            return buf.substring(start).indexOf(' ') == -1;
        }


  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }


        /**
	 * Compute the Julian day number of a date.
	 */
        private static long
        julianDay(int year, int month, int day)
        {
            long        igreg = 15 + 31 * (10 + (12 * 1582));
            int         iy;     // signed, origin-0 year
            int         ja;     // Julian century
            int         jm;     // Julian month
            int         jy;     // Julian year
            long        julday; // returned Julian day number

            /*
	     * Because there is no 0 BC or 0 AD, assume the user wants
	     * the start of the common era if they specify year 0.
	     */
            if (year == 0)
                year = 1;

            iy = year;
            if (year < 0)
                iy++;
            if (month > 2)
            {
                jy = iy;
                jm = month + 1;
            }
            else
            {
                jy = iy - 1;
                jm = month + 13;

            }

            julday = day + (int)(30.6001 * jm);
            if (jy >= 0)
            {
                julday += 365 * jy;
                julday += 0.25 * jy;
            }
            else
            {
                double  xi = 365.25 * jy;

                if ((int)xi != xi)
                    xi -= 1;
                julday += (int)xi;
            }
            julday += 1720995;

            if (day + (31* (month + (12 * iy))) >= igreg)
            {
                ja = jy/100;
                julday -= ja;
                julday += 2;
                julday += ja/4;
            }

            return julday;
        }


        /**
	 * Formats a unit in the underlying system of units.
	 * @param unit		The unit to be formatted.
	 * @param buf		The buffer to append to.
	 * @return		The appended-to buffer.
	 * @throws UnitClassException	The class of the unit is unknown.
	 */
        public StringBuffer
        longFormat(Unit unit, StringBuffer buf)
            throws UnitClassException
        {
            return format(unit, buf, false);
        }


        /**
	 * Test this class.
	 */
        public static void main(String[] args)
            throws Exception
        {
            StandardUnitFormat          parser =
                StandardUnitFormat.instance();
            LineNumberReader    lineInput = new LineNumberReader(
                                    new InputStreamReader(System.in));

            for (;;)
            {
                System.out.print("Enter a unit specification or ^D to quit: ");

                String  spec = lineInput.readLine();
                if (spec == null)
                    break;

                try
                {
                    System.out.println(parser.parse(spec.trim()));
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("");
        }


  final public void multiply() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 23:
      jj_consume_token(23);
      break;
    case 24:
      jj_consume_token(24);
      break;
    case WHITESPACE:
      jj_consume_token(WHITESPACE);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }


  final public Unit nameExpression(UnitDB unitDB) throws ParseException, UnitDBException, UnitSystemException, PrefixDBException {
    Token       t;
    Unit        unit;
    t = jj_consume_token(NAME);
        String  string = t.image;
        double  scale = 1;
        for (unit = getUnit(unitDB, string);
            unit == null;
            unit = getUnit(unitDB, string))
        {
            Prefix      prefix = getPrefix(string);
            if (prefix == null)
            {
                try
                {
                    System.err.println("Unknown unit: \"" + string + '"');
                    unit = UnknownUnit.create(string);
                    break;
                }
                catch (NameException e)
                {}      // shouldn't happen
            }
            scale *= prefix.getValue();
            string = string.substring(prefix.length());
        }
        if (scale != 1)
        {
            unit = new ScaledUnit(scale, unit);
        }
        {if (true) return unit;}
    throw new Error("Missing return statement in function");
  }


  final public double numberExpression() throws ParseException {
    double      value;
    Token       t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                value = Integer.parseInt(t.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 23:
        jj_consume_token(23);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INTEGER:
          // default algorithm is OK
                              t = jj_consume_token(INTEGER);
                        value += new Double("." + t.image).doubleValue();
                        if (value < 0)
                            {if (true) throw new ParseException(
                                "negative sign follows decimal point");}
          break;
        default:
          jj_la1[11] = jj_gen;
          ;
        }
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      break;
    case 23:
      jj_consume_token(23);
      t = jj_consume_token(INTEGER);
                value = new Double("." + t.image).doubleValue();
                if (value < 0)
                    {if (true) throw new ParseException(
                        "negative sign follows decimal point");}
      break;
    case REAL:
      t = jj_consume_token(REAL);
                // Double.parseDouble() *should* exist but doesn't (sigh).
                value = new Double(t.image).doubleValue();
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
        {if (true) return value;}
    throw new Error("Missing return statement in function");
  }


  final public double numericalTerm() throws ParseException {
    double      value1, value2;
    value1 = numberExpression();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case REAL:
      case DIVIDE:
      case 23:
        ;
        break;
      default:
        jj_la1[15] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case REAL:
      case 23:
        value2 = numberExpression();
                value1 *= value2;
        break;
      case DIVIDE:
        jj_consume_token(DIVIDE);
        value2 = numberExpression();
                value1 /= value2;
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
        {if (true) return value1;}
    throw new Error("Missing return statement in function");
  }


        /**
	 * Decodes a unit specification.  An unrecognized unit is made into
	 * an UnknownUnit.
	 * @param spec		The unit specification to be decoded.
	 * @param unitDB	The unit database to use.
	 * @return		The unit corresponding to the specification.
	 * @throws UnitParseException	The unit specification syntax is
	 *				invalid.
	 * @throws SpecificationException	Something's wrong with the
	 *					specification.
	 * @throws UnitDBException	Something's wrong with the unit
	 *				database.
	 * @throws PrefixDBException	Something's wrong with the unit prefix
	 *				database.
	 * @throws UnitSystemException	Something's wrong with the underlying
					system of units.
	 */
        public Unit
        parse(String spec, UnitDB unitDB)
            throws UnitParseException,
                SpecificationException,
                UnitDBException,
                PrefixDBException,
                UnitSystemException
        {
            ReInit(new ByteArrayInputStream(spec.trim().getBytes()));

            try
            {
                Unit    unit = unitSpec(unitDB);
                return unit;
            }
            catch (TokenMgrError e)
            {
                throw new UnitParseException(e.getMessage());
            }
            catch (ParseException e)
            {
                throw new UnitParseException(e.getMessage());
            }
            catch (OperationException e)
            {
                throw new SpecificationException(e.getMessage());
            }
        }


  final public Unit powerExpression(UnitDB unitDB) throws ParseException, OperationException, UnitSystemException, PrefixDBException, UnitDBException {
    double      value;
    Unit        unit;
    Token       t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case REAL:
    case 23:
      value = numberExpression();
                unit=new ScaledUnit(value);
      break;
    case NAME:
    case 25:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NAME:
        unit = nameExpression(unitDB);
        break;
      case 25:
        jj_consume_token(25);
        unit = unitProductList(unitDB);
        jj_consume_token(26);
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case 27:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 27:
          jj_consume_token(27);
          break;
        default:
          jj_la1[8] = jj_gen;
          ;
        }
        t = jj_consume_token(INTEGER);
                    unit = unit.raiseTo(new RationalNumber(Integer.parseInt(t.image)));
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
        {if (true) return unit;}
    throw new Error("Missing return statement in function");
  }


  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


  public void ReInit(StandardUnitFormatTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 24; i++) jj_la1[i] = -1;
  }


/*
 * See <http://www.w3.org/Out-Of-Date/TR/NOTE-datetime-970915.html> for
 * a discussion of a relevant timestamp format.
 */
  final public Date timestampExpression() throws ParseException {
    int         year = 0;
    int         month = 0;
    int         day = 0;
    int         hour = 0;
    int         minute = 0;
    int         zone = 0;       // time zone in minutes
    int         second = 0;
    int         millisecond = 0;
    double      when = 0;
    Token       t;
    int         zoneHour;
    int         zoneMinute;
    t = jj_consume_token(DATE);
        StringTokenizer dateSpec = new StringTokenizer(t.image, "-");

        year = Integer.parseInt(dateSpec.nextToken());
        month = Integer.parseInt(dateSpec.nextToken());
        day = Integer.parseInt(dateSpec.nextToken());
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case WHITESPACE:
    case NAME:
    case TIME:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NAME:
        t = jj_consume_token(NAME);
                    if (!(t.image.equalsIgnoreCase("T")))
                        {if (true) throw new ParseException("invalid timestamp");}
        break;
      default:
        jj_la1[18] = jj_gen;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WHITESPACE:
          jj_consume_token(WHITESPACE);
          break;
        default:
          jj_la1[17] = jj_gen;
          ;
        }
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
        t = jj_consume_token(INTEGER);
                    hour = Integer.parseInt(t.image);
        break;
      case TIME:
        t = jj_consume_token(TIME);
                    StringTokenizer     timeSpec =
                        new StringTokenizer(t.image, ":");

                    hour = Integer.parseInt(timeSpec.nextToken());
                    minute = Integer.parseInt(timeSpec.nextToken());
                    if (timeSpec.hasMoreTokens())
                    {
                        float   sec =
                            new Float(timeSpec.nextToken()).floatValue();
                        second = (int)sec;
                        millisecond = Math.round((sec - second) * 1000);
                    }
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case WHITESPACE:
      case NAME:
      case TIME:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WHITESPACE:
          jj_consume_token(WHITESPACE);
          break;
        default:
          jj_la1[20] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INTEGER:
          t = jj_consume_token(INTEGER);
                        zoneMinute = 0;

                        zoneHour = Integer.parseInt(t.image);

                        if (zoneHour <= -100 || zoneHour >= 100)
                        {
                            zoneMinute = zoneHour % 100;
                            zoneHour /= 100;
                        }

                        zone = zoneHour * 60 + zoneMinute;
          break;
        case TIME:
          t = jj_consume_token(TIME);
                        StringTokenizer zoneSpec =
                            new StringTokenizer(t.image, ":");
                        int     sign = t.image.startsWith("-") ? -1 : 1;

                        zoneHour = Integer.parseInt(zoneSpec.nextToken());
                        zoneMinute = Integer.parseInt(zoneSpec.nextToken());

                        zone = zoneHour*60 + zoneMinute*sign;
          break;
        case NAME:
          t = jj_consume_token(NAME);
                        if (!t.image.equals("UTC") &&
                            !t.image.equals("GMT") &&
                            !t.image.equals("ZULU") &&
                            !t.image.equals("Z"))
                        {
                            {if (true) throw new ParseException("invalid time zone");}
                        }
          break;
        default:
          jj_la1[21] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[22] = jj_gen;
        ;
      }
      break;
    default:
      jj_la1[23] = jj_gen;
      ;
    }
        if (month < 1 || month > 12 ||
            day < 1 || day > 31 ||
            hour < 0 || hour > 23 ||
            minute < 0 || minute > 59 ||
            second < 0 || second > 61 ||
            zone < -1440 || zone > 1440)
        {
            {if (true) throw new ParseException("invalid timestamp");}
        }

        TimeZone        timeZone = TimeZone.getDefault();
        timeZone.setRawOffset(zone*60*1000);
        Calendar        calendar = Calendar.getInstance(timeZone);
        calendar.clear();
        calendar.set(year, month-1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        {if (true) return calendar.getTime();}
    throw new Error("Missing return statement in function");
  }


        /**
	 * Convert broken-out time into a double.
	 */
        private static double
        toDouble(int year, int month, int day,
            int hour, int minute, float second, int zone)
        {
            return (julianDay(year, month, day) - julianDayOrigin) *
                86400.0 + (hour*60 + minute - zone)*60 + second;
        }


  final public Unit unitProductList(UnitDB unitDB) throws ParseException, OperationException, UnitSystemException, PrefixDBException, UnitDBException {
    double      value;
    Unit        unit1, unit2;
    unit1 = powerExpression(unitDB);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case REAL:
      case WHITESPACE:
      case DIVIDE:
      case NAME:
      case 23:
      case 24:
      case 25:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case REAL:
      case WHITESPACE:
      case NAME:
      case 23:
      case 24:
      case 25:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WHITESPACE:
        case 23:
        case 24:
          multiply();
          break;
        default:
          jj_la1[4] = jj_gen;
          ;
        }
        unit2 = powerExpression(unitDB);
                unit1 = unit1.multiplyBy(unit2);
        break;
      case DIVIDE:
        jj_consume_token(DIVIDE);
        unit2 = powerExpression(unitDB);
                unit1 = unit1.divideBy(unit2);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
        {if (true) return unit1;}
    throw new Error("Missing return statement in function");
  }


  final public Unit unitSpec(UnitDB unitDB) throws ParseException, OperationException, UnitSystemException, PrefixDBException, UnitDBException {
    Unit        unit = DerivedUnitImpl.DIMENSIONLESS;
    Date        timestamp;
    double      origin;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case REAL:
    case NAME:
    case 23:
    case 25:
      unit = unitProductList(unitDB);
            if (unit == null)
                unit = DerivedUnitImpl.DIMENSIONLESS;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SHIFT:
        jj_consume_token(SHIFT);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DATE:
          timestamp = timestampExpression();
                        try
                        {
                            unit = new TimeScaleUnit(unit, timestamp);
                        }
                        catch (BadUnitException e)
                        {
                            {if (true) throw new ParseException(
                                "non-time unit with timestamp origin");}
                        }
          break;
        case INTEGER:
        case REAL:
        case 23:
        case 25:
          origin = valueExpression();
                        unit = new OffsetUnit(unit, origin);
          break;
        default:
          jj_la1[0] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(0);
        {if (true) return unit;}
    throw new Error("Missing return statement in function");
  }


  final public double valueExpression() throws ParseException {
    double      value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case REAL:
    case 23:
      value = numericalTerm();
      break;
    case 25:
      jj_consume_token(25);
      value = valueExpression();
      jj_consume_token(26);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
        {if (true) return value;}
    throw new Error("Missing return statement in function");
  }}