/* For more information, see http://www.macdevcenter.com/pub/a/mac/2007/03/06/macfuse-new-frontiers-in-file-systems.html. */ 
#include <errno.h>
#include <fcntl.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>

#define _FILE_OFFSET_BITS 64
#define FUSE_USE_VERSION  26
#include <fuse.h>

#define BUFFER_SIZE 65535
#define USER_AGENT "IbexFUSE/0001"


static const char  *file1_path      = "/time.txt";
static const char  *file2_path      = "/leaderboard.txt";
static const char  *file3_path      = "/leaderboard.csv";
static const char  *file4_path      = "/leaderboard.json";
static const char   host[] = "hexibex.us";
static const char   pfix[]="/engine/ext.rexx/global";
static const int port=80;

size_t file1_size      = 25 /*sizeof(file_content)/sizeof(char) - 1*/;
/*static const*/ size_t file2_size       =1000;
size_t file3_size       =1000;
size_t file4_size       =1000;


static int URLget(const char *host, const int port, const char *identifier, char *buffer2, const int chopheaders)
{  
  int sd,rc,i,j,size;
  char *p;
  char buffer[BUFFER_SIZE];
  
  struct hostent *h;
  struct sockaddr_in servAddr,localAddr;
  
  sprintf(buffer,"GET %s HTTP/1.0\r\nHost: %s\r\nUser-Agent:%s\r\n\r\n",identifier,host,USER_AGENT);
  
  h=gethostbyname(host);
  if(h==NULL) {printf("gethostbyname failed.");return(1);}
  
  servAddr.sin_family = h->h_addrtype;
	memcpy((char *) &servAddr.sin_addr.s_addr, h->h_addr_list[0], h->h_length);
	servAddr.sin_port = htons(port);

	sd = socket(AF_INET, SOCK_STREAM, 0);
	if(sd<0) {printf("cannot open socket ");return(2);}

	localAddr.sin_family = AF_INET;
	localAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	localAddr.sin_port = htons(0);
  
	rc = bind(sd, (struct sockaddr *) &localAddr, sizeof(localAddr));
	if(rc<0) 
	{
		printf("cannot bind port TCP %u\n",port);
		return(3);
	}
				
	rc = connect(sd, (struct sockaddr *) &servAddr, sizeof(servAddr));
	if(rc<0) {printf("cannot connect ");	return(4);}

	rc = write(sd, buffer, strlen(buffer));
  if(rc<0) 
	{   
		printf("cannot send data ");
		close(sd);
		return(5);  
 	}

 	//printf("-- Recieved response:\n\tfrom server: %s, IP = %s,\n\n",host, inet_ntoa(servAddr.sin_addr));
	rc = 0, size = 0;
	memset(buffer2,0,BUFFER_SIZE);
	do
   	{
		memset(buffer,0x0,BUFFER_SIZE);
    	rc = read(sd, buffer, BUFFER_SIZE);
		if( rc > 0)
		{ 
			//printf("%s",buffer);
			strcat(buffer2,buffer);
			size +=rc;
		}
	}while(rc>0);

	//printf("\n   Total recieved response bytes: %d\n",size);
	close(sd);
	
  p=strstr(buffer2,"\r\n\r\n");

	if (chopheaders)
	 j=p-buffer2+4;
	else
	 j=0;
	 
	//printf("j=%i\n",j);
	for (i=0;buffer2[j];i++,j++)
	{
	   //printf("%i,%i,%c\n",i,j,buffer2[j]);
  	 buffer2[i]=buffer2[j];
	}
	buffer2[i]=(char)0;
	
	return 0;
}

static int hello_getattr(const char *path, struct stat *stbuf)
{
    memset(stbuf, 0, sizeof(struct stat));

    if (strcmp(path, "/") == 0) { /* The root directory of our file system. */
        stbuf->st_mode = S_IFDIR | 0755;
        stbuf->st_nlink = 6;
    } else if (strcmp(path, file1_path) == 0) { /* The only file we have. */
        stbuf->st_mode = S_IFREG | 0444;
        stbuf->st_nlink = 1;
        stbuf->st_size = file1_size;
    } else if (strcmp(path, file2_path) == 0) { /* The only other file we have. */
        stbuf->st_mode = S_IFREG | 0444;
        stbuf->st_nlink = 1;
        stbuf->st_size = file2_size;
    } else if (strcmp(path, file3_path) == 0) { /* The only other file we have. */
        stbuf->st_mode = S_IFREG | 0444;
        stbuf->st_nlink = 1;
        stbuf->st_size = file3_size;
    } else if (strcmp(path, file4_path) == 0) { /* The only other file we have. */
        stbuf->st_mode = S_IFREG | 0444;
        stbuf->st_nlink = 1;
        stbuf->st_size = file4_size;
    } else { /* We reject everything else. */
        return -ENOENT;
    }

    return 0;
}

static int hello_open(const char *path, struct fuse_file_info *fi)
{
    if ((fi->flags & O_ACCMODE) != O_RDONLY) { /* Only reading allowed. */
        return -EACCES;
    }
    
    if (strcmp(path, file1_path) == 0)
      return 0;
      
    if (strcmp(path, file2_path) == 0)
      return 0;

    if (strcmp(path, file3_path) == 0)
      return 0;

    if (strcmp(path, file4_path) == 0)
      return 0;

    return -ENOENT;
}

static int hello_readdir(const char *path, void *buf, fuse_fill_dir_t filler, off_t offset, struct fuse_file_info *fi)
{
    if (strcmp(path, "/") != 0) { /* We only recognize the root directory. */
        return -ENOENT;
    }

    filler(buf, ".", NULL, 0);           /* Current directory (.)  */
    filler(buf, "..", NULL, 0);          /* Parent directory (..)  */
    filler(buf, file1_path + 1, NULL, 0); /* The only file we have. */
    filler(buf, file2_path + 1, NULL, 0); /* The only other file we have. */
    filler(buf, file3_path + 1, NULL, 0); /* The only other file we have. */
    filler(buf, file4_path + 1, NULL, 0); /* The only other file we have. */

    return 0;
}

static int hello_read(const char *path, char *buf, size_t size, off_t offset, struct fuse_file_info *fi)
{
    char buffy[BUFFER_SIZE];
    char ident[500];
    int rc=0;
    
    if (strcmp(path, file1_path) == 0)
    {
      sprintf(ident,"%s%s",pfix,file1_path);
      rc=URLget(host,port,ident,buffy,1);
      if (rc!=0); /* some sort of error check, mabye? */
      file1_size=strlen(buffy);
      
      if (offset >= file1_size) /* Trying to read past the end of file. */
          return 0;
  
      if (offset + size > file1_size) /* Trim the read to the file size. */
          size = file1_size - offset;
      
      memcpy(buf, buffy + offset, size); /* Provide the content. */
  
      return size;
    }
    
    if (strcmp(path, file2_path) == 0)
    {
      sprintf(ident,"%s%s",pfix,file2_path);
      rc=URLget(host,port,ident,buffy,1);
      if (rc!=0); /* some sort of error check, mabye? */
      file2_size=strlen(buffy);
      
      if (offset >= file2_size) /* Trying to read past the end of file. */
          return 0;
  
      if (offset + size > file2_size) /* Trim the read to the file size. */
          size = file2_size - offset;
      
      memcpy(buf, buffy + offset, size); /* Provide the content. */
  
      return size;
    }
    
    if (strcmp(path, file3_path) == 0)
    {
      sprintf(ident,"%s%s",pfix,file3_path);
      rc=URLget(host,port,ident,buffy,1);
      if (rc!=0); /* some sort of error check, mabye? */
      file3_size=strlen(buffy);
      
      if (offset >= file3_size) /* Trying to read past the end of file. */
          return 0;
  
      if (offset + size > file3_size) /* Trim the read to the file size. */
          size = file3_size - offset;
      
      memcpy(buf, buffy + offset, size); /* Provide the content. */
  
      return size;
    }
    
    if (strcmp(path, file4_path) == 0)
    {
      sprintf(ident,"%s%s",pfix,file4_path);
      rc=URLget(host,port,ident,buffy,1);
      if (rc!=0); /* some sort of error check, mabye? */
      file4_size=strlen(buffy);
      
      if (offset >= file4_size) /* Trying to read past the end of file. */
          return 0;
  
      if (offset + size > file4_size) /* Trim the read to the file size. */
          size = file4_size - offset;
      
      memcpy(buf, buffy + offset, size); /* Provide the content. */
  
      return size;
    }
    
    
    return -ENOENT;
}

static struct fuse_operations hello_filesystem_operations = {
    .getattr = hello_getattr, /* To provide size, permissions, etc. */
    .open    = hello_open,    /* To enforce read-only access.       */
    .read    = hello_read,    /* To provide file content.           */
    .readdir = hello_readdir, /* To provide directory listing.      */
};


int main(int argc, char **argv)
{
    return fuse_main(argc, argv, &hello_filesystem_operations, NULL);
}