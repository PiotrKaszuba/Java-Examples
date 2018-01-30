package fat8;

import java.math.*;
//klasa systemu
public class FAT8 {
	
//dysk na ktorym zainstalowany jest system
private Disk disk;
//liczba sektorów tablicy FAT
// = liczba_sektorow_dysku * rozmiar_adresu_jap_w_bajtach(1) / rozmiar_sektora, zaokraglone w gore
private int FAT_sectors;
//liczba pozycji(16B) katalogu g³ównego
private int ROOT_space;
//liczba sektorów katalogu g³ównego
// = liczba_pozycji * 16B/rozmiar_sektora , zaokraglone w gore
private int ROOT_sectors;
// liczba sektorow przeznaczonych na dane
// = liczba_sektorow_dysku - liczba_sektorow_katalogu_glownego - liczba_sektorow_tablicy_FAT-1(BOOT_sector)
private int DATA_sectors;
// przesuniecie numeru JAP na numer logiczny sektora
// numery JAP od 1
// = 1(BOOT_sector)+FAT_sectors+ROOT_space
private int JAP_offset;


int dlugosc_wpisu_katalogowego;

//tworzy liste wolnych JAP w tablicy FAT
int numer_pierwszej_wolnej_jap;

// zmienne obslugujace pisanie do pliku
private byte[] write_buffer;
private int buffer_length;
private byte[] write_name;




// gettery sektorow z dysku
private byte[] get_FAT_sector_of_JAP(int JAP)
{
	if (JAP<1 | JAP>64) return null;
	
	
	
	return get_FAT_sector(convert_jap_to_FAT_nr(JAP));
	
}

private byte[] get_FAT_sector(int nr)
{
	if (nr<1 | nr>FAT_sectors) return null;
	
	return disk.send_sector(this.convert_FAT_nr_to_sector_nr(nr));
	
}

private byte[] get_ROOT_sector(int nr)
{
if (nr<1 | nr>ROOT_sectors) return null;
	
	return disk.send_sector(this.convert_ROOT_nr_to_sector_nr(nr));
}


private byte[] get_ROOT_sector_of_entry(int entry)
{
	if (entry<1 | entry>ROOT_space) return null;
	
	
	
	return get_ROOT_sector(convert_entry_to_ROOT_nr(entry));
	
}



private byte[] get_DATA_sector(int JAP)
{
return disk.send_sector(this.convert_DATA_jap_to_sector_nr(JAP));
}
// --------------------------------



// sector saving to disk

private boolean save_ROOT_sector(int nr, byte[] sector)
{
if (nr<1 | nr>ROOT_sectors) return false;
	
	 disk.get_sector(this.convert_ROOT_nr_to_sector_nr(nr), sector);
	return true;
}

private boolean save_FAT_sector(int nr, byte[] sector)
{
if (nr<1 | nr>FAT_sectors) return false;
	
	 disk.get_sector(this.convert_FAT_nr_to_sector_nr(nr), sector);
	return true;
}


private boolean save_ROOT_sector_of_entry(byte[] sector, int entry)
{
	if (entry<1 | entry>ROOT_space) return false;
	
	
	
	 save_ROOT_sector(convert_entry_to_ROOT_nr(entry), sector);
	return true;
	
}

private boolean save_FAT_sector_of_JAP(byte[] sector, int JAP)
{
	if (JAP<1 | JAP>64) return false;
	
	
	
	 save_FAT_sector(convert_jap_to_FAT_nr(JAP), sector);
	return true;
	
}

private boolean save_DATA_sector(byte[] sector, int JAP)
{
	if (JAP+JAP_offset>disk.getSector_number() | JAP < 1) return false;
	disk.get_sector(convert_DATA_jap_to_sector_nr(JAP), sector);
    return true;
}
//--------------------------

// converting sector indexes

private int convert_DATA_jap_to_sector_nr(int JAP)
{
	return JAP+this.JAP_offset;
}

private int convert_ROOT_nr_to_sector_nr(int nr)
{
	return nr+1+FAT_sectors;
}

private int convert_FAT_nr_to_sector_nr(int nr)
{
	return nr+1;
}

private int convert_entry_to_ROOT_nr(int entry)
{
	return (entry-1)*dlugosc_wpisu_katalogowego/disk.getSector_size() +1;
}

private int convert_jap_to_FAT_nr(int JAP)
{
	return (JAP-1)/disk.getSector_size() +1;
}
//-----------------------------------------------





// funkcje dla wpisow (entry)

//zwraca pierwsza pozycje(bajt) konkretnego wpisu w sektorze w ktorym sie znajduje
private int get_entry_first_position_in_block(int global_entry)
{
	return (global_entry-1)%(disk.getSector_size()/dlugosc_wpisu_katalogowego)*dlugosc_wpisu_katalogowego;
}

//zwraca bajt na pozycji nr w kolejnym wpisie o numerze entry w danym sektorze
private byte check_entry_field(byte[] sector, int entry, int nr)
{
	return sector[dlugosc_wpisu_katalogowego*(entry-1)+nr-1];
}

// ROOT wpis -> 1(atrybut) 0 nieuzyty, 1 skasowany, 2 plik; 8 nazwa; 1 numer pierwszej JAP; 2 rozmiar
// zwraca 0 jezeli plik nie istnieje lub jego pozycje jesli istnieje
private int file_exists(byte[] name)
{
	
	
	byte[] buffer;
	int entry = 1;
	
	while(entry <= ROOT_space)
	{
		buffer = get_ROOT_sector_of_entry(entry);
	
	
	
	for (int i=1; i<= disk.getSector_size()/dlugosc_wpisu_katalogowego;i++)
	{
		for (int j=2; j<=9; j++)
			
		{
			
			if ( check_entry_field(buffer, i, j) != name[j-2])
			{
				break;
			}
			
			
			if (j==9 && check_entry_field(buffer, i, 1) == 2) return entry;
			
		}
		
		
	   entry ++;
		
	}
	}
	return 0;
}


// zwraca 0 jezeli nie ma miejsca i 1-max.nr jezeli jest miejsce i konkretne wolne wtedy
private int get_root_space()
{
	byte[] buffer;
	int entry = 1;

	while(entry <= ROOT_space)
	{
		buffer = get_ROOT_sector_of_entry(entry);
		for (int i=1; i<= disk.getSector_size()/dlugosc_wpisu_katalogowego;i++)
		{
			if ( check_entry_field(buffer, i, 1) != 2) 
			{
				
		       return entry;
			}
			else 
			{
				entry++;
			}
		}
	}
	return 0;
}


// edytuje wpis katalogowy przekazany w parametrze
// null nie zmienia lub -1
private byte[] edit_entry(byte[] entry, byte att, byte[] name, byte first_jap, int rozmiar )


{
	if (att != -1) entry[0] = att;
	if (name != null) 
		for(int i=1, j=0; i<=8;i++, j++ )
		{
			entry[i] = name[j];
		}
		
	
	if (first_jap != -1) entry[9] = first_jap;
	if (rozmiar != -1 ) entry[10] = (byte) (rozmiar/256);
	if (rozmiar != -1 ) entry[11] = (byte) (rozmiar%256);
	
	return entry;

}


// zbiorcza funkcja edycji wpisu - podaje sie numer wpisu - najlepiej uzyskac go przez nazwe - file_exists(name)
//jesli entry = 0 to przerywa sie
private boolean complex_entry_edit(int entrynr, int att, byte[]name, int first_jap, int rozmiar)
{
	if (entrynr==0) return false;
	
    byte[] buffer;
	
	buffer = get_ROOT_sector_of_entry(entrynr);
	
	byte[] entry = get_specific_entry(buffer, entrynr);
	
	entry = edit_entry(entry, (byte) att, name,(byte)first_jap, (byte)rozmiar);
	
	buffer = save_entry_to_block(buffer, entry, entrynr);
	
	save_ROOT_sector_of_entry(buffer, entrynr);
	
	return true;
}


// zwraca konkretny wpis katalogowy 
// bierze dany sektor FAT a potem odszukuje blok dzieki funkcji modulo z numeru ogolnego wpisu
private byte[] get_specific_entry(byte[] sector, int global_entry)
{
	byte[] entry= new byte[16];
	
	for(int i=get_entry_first_position_in_block(global_entry),j=0;j<16;i++,j++)
	{
		entry[j]=sector[i];
	}
	return entry;
}

// zapisuje do bufora sektoru wyedytowany wpis
private byte[] save_entry_to_block(byte[] sector, byte[] entry, int global_entry)
{
	
	for(int i=get_entry_first_position_in_block(global_entry),j=0;j<16;i++,j++)
	{
	sector[i]=entry[j];
	}
	
	
	return sector;
}

//----------------------------------------------------





//Interfejs klasy - metody uzywane (oprocz wypisan, konwertowan i konstruktora):


// tworzy plik o podanej nazwie bez przydzielonego miejsca
public boolean create_file(String Sname)
{
	byte[] name;
	name = convert_name(Sname);
	
	if (file_exists(name) != 0) return false;
	int free_space = get_root_space();
	if (free_space == 0 ) return false;

	if(!complex_entry_edit(free_space, 2, name, 0, 0)) return false;
	
	
	return true;
	
}


// pisze do bufora, kiedy ten sie wypelni to zapisuje do pliku
// jezeli przed zapelnieniem bufora zamykamy plik to dopisujemy mniej niz pelny sektor
public boolean write_to_file(String Sname, String Stext)
{
	
	
	byte[] name;
	name = convert_name(Sname);
	
	byte[] bajty;
	bajty = convert_text(Stext);
	
	
	if (name != write_name) 
	{
		this.buffer_length=0;
		this.write_name = name;
	}
	
	for (int i = 0; i < bajty.length; i++)
	{
		this.write_buffer[buffer_length] = bajty[i];
		buffer_length++;
		if (buffer_length==disk.getSector_size()) 
			{
			if(!save_file(name, write_buffer, disk.getSector_size())) return false;
			buffer_length = 0;
			}
	}
	return true;
}


//zamyka plik - i odrazu zapisuje w nim bufor - poprawna metoda zapisywania
public boolean close_file()
{
	
	if(save_file(write_name, write_buffer, buffer_length)) {
		write_name = null;
		return true;
	}
	
	else 
		{
		write_name = null;
		return false;
		}
}

//usuwa dany plik - usuwa jedynie wpis w tablicy FAT 
//dane pozostaja ale moge zostac zastapione kolejnym plikiem
public boolean delete_file(String Sname)
{
byte[] name;
name = convert_name(Sname);

int entry = file_exists(name);
if (!complex_entry_edit(entry, 1, null, -1, -1)) return false;
int jap=numer_pierwszej_wolnej_jap;
int jap_before=-1;
while(jap!=0)
{
	jap_before=jap;
	jap=FAT_next_JAP(jap);
}
FAT_chain(jap_before,this.get_ROOT_sector_of_entry(entry)[this.get_entry_first_position_in_block(entry)+9]);



return true;
	
}

public boolean rename_file(String Sname, String newSname)
{
	byte[] name;
	name = convert_name(Sname);
	
	byte[] newname;
	newname = convert_name(newSname);
	
	int entry = file_exists(name);
	if (!complex_entry_edit(entry, -1, newname, -1, -1)) return false;
	return true;
}

//wysyla plik do pamieci RAM w celu czytania/wykonania
public byte[] open_file(String Sname)
{
	byte[] name;
	name = convert_name(Sname);
	
	int entry = file_exists(name);
	if (entry == 0) return null;
	byte[] buffer = get_ROOT_sector_of_entry(entry);
	
	
	int rozmiar = buffer[get_entry_first_position_in_block(entry)+10] * 256;
	rozmiar += buffer[get_entry_first_position_in_block(entry)+11];
	
	byte[] sent = new byte[rozmiar+2];
	sent[0] = (byte) (rozmiar/256);
	sent[1] = (byte) (rozmiar%256);
	
	int i = rozmiar%disk.getSector_size();
	int j = 2;
	int jap = buffer[get_entry_first_position_in_block(entry)+9];
	while (jap!=0)
	{
		buffer = get_DATA_sector(jap);
		for (int k=0; k<disk.getSector_size(); k++)
		{
			sent[j] = buffer[k];
			j++;
		}
		jap=FAT_next_JAP(jap);
	}
   if (i>0)
   { 
	    buffer = get_DATA_sector(jap);
	    for (int k=0; k<i; k++)
	    {
	    	sent[j] = buffer[k];
			j++;
	    }
	 
   }

	
	return sent;
}

//-----------------------------------------------------------

// wewnetrzne metody przetwarzania


// dopisuje na koncu pliku o nazwie name, dane data o dlugosci size, jesli potrzeba powieksza lancuch jap, zwieksza rozmiar we wpisie
// zwraca falsz jezeli nie ma wolnej jap, lub zla nazwa podana
private boolean save_file(byte[] name, byte[] data, int size )
{
	int entry = file_exists(name);
	if (entry == 0) return false;
	byte[] buffer = get_ROOT_sector_of_entry(entry);
	
	int rozmiar = buffer[get_entry_first_position_in_block(entry)+10] * 256;
	rozmiar += buffer[get_entry_first_position_in_block(entry)+11];
	
	
	int first_jap = buffer[get_entry_first_position_in_block(entry)+9];
	int jap = first_jap;
	
	int free_in = disk.getSector_size() - rozmiar%disk.getSector_size();
	
	int jap_before=-1;
	
	while(jap!=0)
	{
		jap_before=jap;
		jap = FAT_next_JAP(jap);
	}
	
	
	
	
	
	
	if (first_jap==0)
	{
		if(numer_pierwszej_wolnej_jap == 0) return false;
		first_jap = numer_pierwszej_wolnej_jap;
		jap_before = numer_pierwszej_wolnej_jap;
		jap = 0;
		numer_pierwszej_wolnej_jap = FAT_next_JAP(numer_pierwszej_wolnej_jap);
		FAT_chain(jap_before, jap);
		
	}
	else 
	{
		if(free_in==disk.getSector_size())
		{
			if(numer_pierwszej_wolnej_jap == 0) return false;
			FAT_chain(jap_before, numer_pierwszej_wolnej_jap);
			jap_before = numer_pierwszej_wolnej_jap;
			jap = 0;
			numer_pierwszej_wolnej_jap = FAT_next_JAP(numer_pierwszej_wolnej_jap);
			FAT_chain(jap_before, jap);
		
		}
	}
	
	
	
	
	
	
	
	int i = size;
	
	
	
	buffer=this.get_DATA_sector(jap_before);
	for ( int k=rozmiar%disk.getSector_size(); k<disk.getSector_size(); k++)
	{
		buffer[k] = data[size-i];
		i--;
		if(i==0) break;
	}
	if(!this.save_DATA_sector(buffer, jap_before)) return false;
	
	
	if (i>0)
	{
		if(numer_pierwszej_wolnej_jap == 0) return false;
		FAT_chain(jap_before, numer_pierwszej_wolnej_jap);
		jap_before = numer_pierwszej_wolnej_jap;
		jap = 0;
		numer_pierwszej_wolnej_jap = FAT_next_JAP(numer_pierwszej_wolnej_jap);
		FAT_chain(jap_before, jap);
		buffer=this.get_DATA_sector(jap_before);
		
		for(int k=0;i>0;i--,k++)
		{
			buffer[k] = data[size-i];
		}
		if(!this.save_DATA_sector(buffer, jap_before)) return false;
	}
	
	
	
	rozmiar += size;
	this.complex_entry_edit(entry, -1, null, first_jap, rozmiar);
	return true;
}


//odczytaj z tablicy FAT kolejny adres JAP
private int FAT_next_JAP(int JAP_number)
{
	return  get_FAT_sector_of_JAP(JAP_number)[(JAP_number-1)%disk.getSector_size()];
}

// wpisz do tablicy FAT do JAP kolejny adres JAP
private void  FAT_chain(int JAP_number, int next_JAP)
{
	byte[] buffer = get_FAT_sector_of_JAP(JAP_number);
	buffer[(JAP_number-1)%disk.getSector_size()] = (byte) next_JAP;
	save_FAT_sector_of_JAP(buffer, JAP_number);
}


//-------------------------------------


//konstruktor i metody pomocnicze

private void init_boot_sector(){
	byte[] boot = new byte[disk.getSector_size()];
	//instrukcja skoku do programu ladujacego
	for (int i=0x0;i<=0x7;i++)
	{
		boot[i]=(byte)0;
	}
	// nazwa systemu
	boot[8]=(byte) 'F';
	boot[9]=(byte) 'A';
	boot[0xA]=(byte) 'T';
	boot[0xB]=(byte) '8';
	
	for(int i=0xC;i<=0xF;i++)
	{
		boot[i]=(byte)' ';
	}
	

	// LITTLE ENDIAN
	// rozmiar sektora 
	boot[0x10] = (byte) (disk.getSector_size()%256) ;
	boot[0x11] = (byte)  (disk.getSector_size()/256);		
	// ile sektorow na JAP
	boot[0x12] = (byte) 1;	
	// liczba sektorow zarezerwowanych na poczatku dysku
	boot[0x13] = (byte)   ((FAT_sectors+ROOT_sectors+1)%256);	
	boot[0x14] = (byte)  ((FAT_sectors+ROOT_sectors+1)/256);	
	// Pojemnosc katalogu glownego(pozycji)
	boot[0x15] = (byte)   (this.ROOT_space%256);
	boot[0x16] = (byte)   (this.ROOT_space/256);	
	//calkowita liczba sektorow
	boot[0x17] = (byte)   (disk.getSector_number()%256);	
	boot[0x18] = (byte)   (disk.getSector_number()/256);	
	//wielkosc FAT (w sektorach)
	boot[0x19] = (byte)   (this.ROOT_sectors%256);	
	boot[0x1A] = (byte)   (this.ROOT_sectors/256);	
	
	
	
	disk.get_sector(1, boot);
}

private void init_FAT_sectors(){
	byte[] buffer;
	int k=2;
	for(int i=1; i<= FAT_sectors;i++)
	{
	   buffer =  this.get_FAT_sector(i);
	   for (int j=0;j<disk.getSector_size();j++)
	   {
		   if(k<disk.getSector_number()-this.JAP_offset)
		   {
			   buffer[j]=(byte) k;
			   k++;
		   }
		   else {
			   buffer[j]=0;
		   }
	   }
	   this.save_FAT_sector(i, buffer);
	}
}


//podaje sie dopuszczalna liczbe pozycji katalogu i przekazuje dysk na ktorym zainstalowany jest system
//ustawia boot_sector
public FAT8(){
    int liczba_pozycji_katalogu = 20;
	dlugosc_wpisu_katalogowego = 16;
	
	disk= new Disk(32,64);
	ROOT_space=liczba_pozycji_katalogu;
	FAT_sectors = (int) Math.ceil(disk.getSector_number()*1/disk.getSector_size());
	ROOT_sectors = (int) Math.ceil(ROOT_space*dlugosc_wpisu_katalogowego/disk.getSector_size());
	DATA_sectors = disk.getSector_number() - ROOT_sectors - FAT_sectors - 1;
	JAP_offset = 1+FAT_sectors+ROOT_sectors;
	
	write_buffer = new byte[disk.getSector_size()];
	write_name = new byte[8];
	write_name = null;
	
	numer_pierwszej_wolnej_jap = 1;
	
	init_boot_sector();
	init_FAT_sectors();
	
	
}

//----------------------------------


//metody pomocniczne aby mozna bylo podawac nazwy w stringach
//trzeba podac nazwe w funkcji konwertujacej


//fixes length to 8
public byte[] convert_name (String name)
{
	
	char[] temp = name.toCharArray();
	byte[] ret = new byte[8];
	
	for (int i=0; i<8 ; i++)
	{
		
		ret[i] =0;
		
	}
	for (int i=0; i<temp.length && i<8 ; i++)
	{
		ret[i] = (byte) temp[i];
		
	}
	return ret;
	
}

public byte[] convert_text (String name)
{
	char[] temp = name.toCharArray();
	byte[] ret = new byte[temp.length];
	for (int i=0; i<temp.length; i++)
	{
		ret[i] = (byte) temp[i];
		
	}
	return ret;
}

//----------------------------------

// przechwytywanie dysku bezposrednio lub drukowanie konkretnych obszarow
public Disk get_disk()
{
	return this.disk;
}


public void print_BOOT(boolean charmode)
{
	disk.print_sector(1, charmode);
}

public void print_FAT(boolean charmode)
{
	disk.print_group(this.convert_FAT_nr_to_sector_nr(1), FAT_sectors, charmode);
}

public void print_ROOT(boolean charmode)
{
	disk.print_group(this.convert_ROOT_nr_to_sector_nr(1), ROOT_sectors, charmode);
}

public void print_DATA(boolean charmode)
{
	disk.print_group(this.convert_DATA_jap_to_sector_nr(1), DATA_sectors, charmode);
}

public boolean print_File_ROOT(String Sname, boolean charmode)
{
	byte[] name;
	name = convert_name(Sname);
	
	int entry = file_exists(name);
	if (entry == 0) return false;
    disk.print_sector(this.convert_ROOT_nr_to_sector_nr(this.convert_entry_to_ROOT_nr(entry)), charmode);
    return true;
}
// ---------------------------------------------------------
}
