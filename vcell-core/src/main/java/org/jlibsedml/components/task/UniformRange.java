package org.jlibsedml.components.task;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UniformRange extends Range {

    public enum UniformType {
        LINEAR("linear"), LOG("log");
        private String text;

        UniformType(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static UniformType fromString(String text) {
            if (text == null) return null;
            for (UniformType b : UniformType.values()) {
                if (!text.equalsIgnoreCase(b.text)) continue;
                return b;
            }
            return null;
        }
    }

    private double start;
    private double end;
    private int numberOfSteps;
    private UniformType type;

    public UniformRange(SId id, double start, double end, int numberOfPoints) {
        super(id);
        this.start = start;
        this.end = end;
        this.numberOfSteps = numberOfPoints;
        this.type = UniformType.LINEAR;
    }

    public UniformRange(SId id, double start, double end,
            int numberOfPoints, UniformType type) {
        super(id);
        this.start = start;
        this.end = end;
        this.numberOfSteps = numberOfPoints;
        if (type == null) {
            this.type = UniformType.LINEAR;
        } else if (type == UniformType.LINEAR) {
            this.type = type;
        } else if (type == UniformType.LOG) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        if (UniformType.LOG.equals(getType()) && start == 0 ){
            throw new IllegalArgumentException("Invalid argument for 'start'- must be > 0");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(end);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + numberOfSteps;
        temp = Double.doubleToLongBits(start);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        UniformRange other = (UniformRange) obj;
        if (Double.doubleToLongBits(end) != Double.doubleToLongBits(other.end))
            return false;
        if (numberOfSteps != other.numberOfSteps)
            return false;
        if (Double.doubleToLongBits(start) != Double
                .doubleToLongBits(other.start))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    @Deprecated
    public int getNumberOfPoints() {
        return this.numberOfSteps;
    }

    public int getNumberOfSteps(){
        return this.numberOfSteps;
    }

    public UniformType getType() {
        return type;
    }

    @Override
    public int getNumElements() {
        return numberOfSteps;
    }

    @Override
    public double getElementAt(int index) {
        if (index < 0 || index > numberOfSteps - 1) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "{0} is an invalid index. It must be between 0 and {1}.",
                    index, numberOfSteps - 1));
        }
        
        if (type == UniformType.LINEAR) {
            return start + ((end - start) / (numberOfSteps - 1))
                    * ((double) index);
        } else {
            return start
                    * Math.pow(end / start, ((double) index)
                            / (numberOfSteps - 1));
        }
    }

    @Override
    public String getElementName() {
        return SedMLTags.UNIFORM_RANGE_TAG;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        params.add(String.format("start=%f", this.start));
        params.add(String.format("end=%f", this.end));
        params.add(String.format("numberOfPoints=%d", this.numberOfSteps));
        params.add(String.format("type=%s", this.type));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
