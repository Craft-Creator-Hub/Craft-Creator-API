package fr.en0ri4n.craftcreator.api.init.definitions;

public class CoreBlockPos
{
    private final int x;
    private final int y;
    private final int z;

    public CoreBlockPos(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }
}
