# Comic from Tweets

## 課題設定
かなりの数の人がTwitterで漫画を投稿したり，読んだりしている．
しかし，漫画を読みたいときに，普通のツイートばかりで漫画が全然見つからないことがある．
方法としては,
- 画像ツイートのなかで漫画の投稿にありそうな単語で検索する
- 漫画を投稿する人だけのリストを作り，リスト内のツイートをみる
というものが考えられるが，どちらも個人でやるには少し手間がかかるので，
  ツイートから漫画のみを抽出するということがサービスとして成立するのではないかと考えた．
  
## 概要
Twitterの`tweets/search/recent`apiを用いて，キーワードに合わせて最近のツイートの中から漫画だと思われるものを抽出する．
検索条件は`"<入力テキスト> 漫画 話"`
取得したデータの中の一つ目のツイートのデータを画面に表示


## 参考にしたサイト
- https://zenn.dev/nemuki/articles/my-first-android-app#api-%E3%82%A2%E3%82%AF%E3%82%BB%E3%82%B9%E3%81%AE%E7%94%A8%E3%81%AE%E8%A8%98%E8%BF%B0%E3%82%92%E8%BF%BD%E5%8A%A0
- https://developer.twitter.com/en/docs/twitter-api/data-dictionary/object-model/media