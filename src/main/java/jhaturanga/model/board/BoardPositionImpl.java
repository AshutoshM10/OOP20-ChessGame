package jhaturanga.model.board;

public class BoardPositionImpl implements BoardPosition {

    private int xPosition;
    private int yPosition;

    public BoardPositionImpl(final int xPosition, final int yPosition) {
	this.xPosition = xPosition;
	this.yPosition = yPosition;
    }

    @Override
    public final int getX() {
	return this.xPosition;
    }

    @Override
    public final int getY() {
	return this.yPosition;
    }

    @Override
    public final void setX(final int xNewPos) {
	this.xPosition = xNewPos;
    }

    @Override
    public final void setY(final int yNewPos) {
	this.yPosition = yNewPos;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + xPosition;
	result = prime * result + yPosition;
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final BoardPositionImpl other = (BoardPositionImpl) obj;
	if (xPosition != other.xPosition) {
	    return false;
	}
	if (yPosition != other.yPosition) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "BoardPositionImpl [xPosition=" + xPosition + ", yPosition=" + yPosition + "]";
    }

}
