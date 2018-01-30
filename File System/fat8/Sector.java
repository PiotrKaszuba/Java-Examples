package fat8;
// Pojedynczy sektor na dysku, ktory zostaje w calosci odczytany/zapisany
public class Sector {
public static int size;
private byte[] space;

// wielkosc sektora przyjmuje wartosc ustalona przy tworzeniu dysku
public Sector()
{
	space=new byte[Sector.size];
}

//drukuje sektor w postaci heksadecymalnej
public void print()
{
	for(int i=0;i<Sector.size;i++)
	{
		 System.out.print(String.format("-%02X", (int) space[i]));
	}
	
}
public byte[] getSpace() {
	return space;
}
public void setSpace(byte space, int logic_index) {
	this.space[logic_index] = space;
}



}
