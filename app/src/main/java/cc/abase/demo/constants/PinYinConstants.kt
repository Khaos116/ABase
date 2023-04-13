package cc.abase.demo.constants

/**
 * 多音字处理
 * //https://baike.baidu.com/tashuo/browse/content?id=6985b0e608a4f4b9c541d03a
 * Author:Khaos
 * Date:2023/4/13
 * Time:16:57
 */
object PinYinConstants {
  //将
  //jiāng  将功赎罪  将心比心 将军
  //jiàng  奥运健将  将门虎子 将才
  //qiāng  将进酒

  //薄
  //báo 薄饼  薄纸  薄厚
  //bó  单薄  厚此薄彼
  //bò  薄荷

  //血
  //xiě 吐了一口血
  //xuè 鲜血 血缘

  //和
  //hé（第2声）：表示共同、协调的意思，比如“和平”、“和谐”、“和解”等。
  //hè（第4声）：表示“合”、“融合”的意思，比如“曲高和寡”、“和诗一首”等。
  //hú（第2声）：表示麻将中胡牌的意思，比如“和了”、“自摸和”等。
  //huó（第2声）：表示用手揉搓面粉等的动作，比如“和面”。
  //huò（第4声）：表示若干、几个的意思，比如“和稀泥”等。
  //huo（第4声）：作为后缀使用读轻声，比如“暖和”、“搅和”等。
  //xuān（第1声）：通“宣”，意思是宣布，比如“汝不和吉言于百姓，惟汝自生毒。”。

  //啊
  //ā（第1声）：啊，好美的风景啊！
  //á（第2声）：你啊，真会开玩笑！
  //ǎ（第3声）：你在说啥啊？我没听懂。
  //à（第4声）：啊，原来是这样啊！
  //ya（轻声）：这件衣服好看啊（读作hǎokànyā）！
  //wa（轻声）：你要去旅游啊（读作nǐyàoqùlǚyóuwa）？
  //na（轻声）：这道菜好吃啊（读作zhèdàocàihǎochīna）！
  //nga（轻声）：今天真冷啊（读作jīntiānzhēnlěngnga）！
  //ra（轻声）：你咋啦啊（读作nǐzǎlāra）？
  //za（轻声）：这个故事好有意思啊（读作zhègègùshìhǎoyǒuyìsìza）！

  val _将 = mutableListOf(
    "将功赎罪" to "jiāng",
    "奥运健将" to "jiàng",
    "将进酒" to "qiāng",
    "将心比心" to "jiāng",
    "将门虎子" to "jiàng",
    "将军" to "jiāng",
    "将才" to "jiàng",
  )

  val _薄 = mutableListOf(
    "薄饼" to "báo",
    "单薄" to "bó",
    "薄荷" to "bò",
    "薄纸" to "báo",
    "厚此薄彼" to "bó",
    "薄厚" to "báo",
  )

  val _血 = mutableListOf(
    "鲜血" to "xuè",
    "吐了一口血" to "xiě",
    "血缘" to "xuè",
  )

  val _和 = mutableListOf(
    "和平" to "hé",
    "曲高和寡" to "hè",
    "和了" to "hú",
    "和面" to "huó",
    "和稀泥" to "huò",
    "暖和" to "huo",
    "汝不和吉言于百姓，惟汝自生毒" to "xuān",
    "和谐" to "hé",
    "和诗一首" to "hè",
    "自摸和" to "hú",
    "搅和" to "huo",
    "和解" to "hé",
  )

  val _啊 = mutableListOf(
    "啊，好美的风景啊" to "ā",
    "你啊，真会开玩笑" to "á",
    "你在说啥啊？我没听懂" to "ǎ",
    "啊，原来是这样啊" to "à",
    "这件衣服好看啊" to "ya",
    "你要去旅游啊" to "wa",
    "这道菜好吃啊" to "na",
    "今天真冷啊" to "nga",
    "你咋啦啊" to "ra",
    "这个故事好有意思啊" to "za",
  )
}