//storage/emulated/0/Download目录
//=========没有SD卡读写权限=========
//1.可以在Download目录写文件和文件夹
//2.可以读到自己APP创建的文件和文件夹以及文件内容
//3.只能读到其他APP创建的文件夹，读不到文件
//=========有SD卡读写权限=========
//能正常读取文件和文件夹
//⭐基于以上特性⭐可以实现一下功能：
//1️⃣通过创建文件夹的命名实现简单的数据备份[其他APP可以读文件夹名称]
//2️⃣可以创建文件夹和文件备份APP的数据，以便卸载后重新安装可以恢复
