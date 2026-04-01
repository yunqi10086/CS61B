# Gitlet 设计文档

## 1 对核心文件夹.gitlet的说明

实现所需的文件都存放于.gitlet文件夹下，其主要包含以下文件夹:

* **commited**  \
下含众多文件，用于存放commit的历史\
每个文件的**文件名为序列化后的commit的哈希值**，即```sha1(serialize(Commit))``` \
每个文件的**文件内容为序列化后的commit**，即```serialize(Commit)``` \
**用途：从commit的哈希值映射到commit对象**

* **staged** \
下含众多文件，用于存放被add，但未被commit的文件副本。文件被commit后相应的会从该文件中删除该副本\
每个文件的**文件名为被add的文件本身的文件名**\
每个文件的**文件内容为对应文件的哈希值**，即```sha1(readContent(File))```\
**用途：从文件名映射到文件的哈希值**

* **removed** \
下含众多文件，用于存放即将被删除的文件
  每个文件的**文件名为待删除文件本身的文件名**\
  每个文件的**文件内容为待删除文件的哈希值**，即```sha1(readContent(File))```\
**用途：从文件名映射到文件的哈希值**

* **blobs** \
下含众多文件，用于存放所有被add过的文件的副本
每个文件的**文件名为待查找文件的哈希值**，即```sha1(readContents(File))```\
每个文件的**文件内容为序列化的待查找文件内容**，即```serialize(readContents(File))```\
**用途：从文件哈希值映射到文件内容**

* **branches** \
下含众多文件，用于存放不同的分支
每个文件的**文件名为分支的名字**\
每个文件的**文件内容为序列化后的commit的哈希值**，即```sha1(serialize(Commit))```\
**用途： 从branch_name(String)映射到(该branch所指向的)commit的哈希值**

* **head** \
此为单文件，文件名为head，文件内容为序列化的HEAD指针，即```serialize(head)```\
**用途：使得关闭程序重新打开后能恢复head指针**

## 2 .gitlet文件夹下的一般文件查找过程
* 文件名 --(java.Util.join)>> 文件
* 文件 --(java.Util.readContentsAsString)>> 文件内容(Srting)
* 文件 --(java.Util.readContents)>> 文件内同(byte[])
* 文件名 --(staged)>> 文件哈希值
* 文件名 --(removed)>> 文件哈希值
* 文件哈希值 --(blobs)>> 文件名
* commit哈希值 --(commited)>> serialized_commit
* commit --(java.Util.serialize)>> commit哈希值
* branch_name --(branches)>> serialized_commit

## 3 对Commit类主要成员的介绍
* ```List<String> parent```
用于保存父Commit的哈希值，即```sha1(serialize(Commit))```\
* ```TreeMap<String, String> map```
key为文件名称，即```filename```\
value为文件内容的哈希值，即```sha(serialize(readContents(File)))```\
用于保存该Commit所包含的各种文件\
* ```message``` 保存commit信息
* ```timestamp``` 保存commit时间

## 4 对Repository文件的介绍
* 实现核心Commands，包括```init``` ```add``` ```commit``` 
```rm``` ```log``` ```global_log``` ```find``` ```status```
```checkout``` ```branch``` ```rm-branch``` ```reset``` ```merge```


