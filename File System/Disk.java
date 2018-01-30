package fat8;

public class Disk {
private int sector_size;
private int sector_number;
private byte[] disk;
private byte[] sector_read_buffer;
public Disk(int Sector_size, int Sector_number)
{
	//ustalenie rozmiaru sektora
	sector_size = Sector_size;
	
	//ilosc sektorow zapisana
	this.setSector_number(Sector_number);
	
	//przydzial pamieci - sektorow o wymiarze ustalonym
	this.disk = new byte[this.sector_number*this.sector_size];
	
	
	sector_read_buffer = new byte[sector_size];
	
	Sector.size = Sector_size;
}



// funkcja do wyslania do pamieci RAM sektoru
public byte[] send_sector(int index)
{
	if (index > sector_number | index < 1) return null;
	for (int i=(index-1)*sector_size, j=0; i< index * sector_size; i++,j++)
	{
		sector_read_buffer[j] = disk[i];
	}
	return sector_read_buffer;
}

// zapisanie sektoru z pamieci RAM
public void get_sector(int index, byte[] sector)
{
	for (int i=(index-1)*sector_size, j=0; i< index * sector_size; i++,j++)
	{
		disk[i] = sector[j];
	}
	
	
	
}


public int getSector_number() {
	return sector_number;
}

public void setSector_number(int sector_number) {
	this.sector_number = sector_number;
}


public void print_group(int start, int length, boolean charmode)
{
	for (int i=start; i <= start + length -1; i++)
	{
		print_sector(i, charmode);
	}
}




public void print_sector(int index, boolean charmode)
{
	byte[] sector = send_sector(index);
	for (int i =0; i<Sector.size; i++)
	{
		if(charmode) System.out.print((char)sector[i]);
		else System.out.print(sector[i] + " ");
	}
	System.out.println();
}


public void print_disk(boolean charmode)
{
	for (int i =1; i<=sector_number;i++)
	{
		print_sector(i, charmode);
	}
}



}
