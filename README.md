# Comic from Tweets

## 課題設定
かなりの数の人がTwitterで漫画を投稿したり，読んだりしている．
しかし，漫画を読みたいときに，普通のツイートばかりで漫画が全然見つからないことがある．
方法としては,
- 画像ツイートのなかで漫画の投稿にありそうな単語で検索する
- 漫画を投稿する人だけのリストを作り，リスト内のツイートをみる<br>
というものが考えられるが，どちらも個人でやるには少し手間がかかるので，
  ツイートから漫画のみを抽出するということがサービスとして成立するのではないかと考えた．
  
## 概要
<img src="https://i.imgur.com/4XC7lXLh.png" width="30%"><br>
1. Twitterの`tweets/search/recent`apiを用いて，キーワードに合わせて最近のツイートの中から漫画だと思われるものを抽出する．
2. ユーザは検索窓に読みたい漫画のキーワードを入れ，検索する． 
3. 検索条件は`"入力テキスト" 漫画 話`と設定した．
4. 取得したデータの中の一つ目のツイートのデータを画面に表示

## 設計
ツイートを閲覧する時と違い，漫画を読むときは画像がメインなので，画像が大きく表示されるように設計した．<br>
UIはInstagramを参考にした．ユーザに写真閲覧アプリとして最も用いられているのはInstagramであり，
同じようなUIにすることによって，ユーザが操作に戸惑うことが少なく便利であると考えたからだ．

## 難所
- 題材選び<br>
  「写真投稿サービスのAPIを利用したサービス」という課題で，そもそもあまり多くの種類のAPIを見つけることができなかったため
  できることがかなり限られてしまい，これらのAPIを用いてどのような価値をユーザに提供できるか考えるのに苦労した．
  
- 複数画像の表示<br>
  Twitter APIの仕様上，検索結果としてのツイートの，ツイートのデータ，ユーザのデータ，画像のデータなどが別々のリストとして
  返されるため，どの画像がどのツイートのものかを判定するのが少し難しかった．(もしかしたら仕様書の確認不足かもしれない)
  実際には画像のurlを見比べて，同一ツイートの画像にはurlに共通部分があることを発見し，それを判定に用いた．

## 展望
- キーワードの最適化<br>
  現在の検索ワードは漫画を抽出するのに最適されているとは言い難く，実際漫画ではない画像が表示されてしまうこともある．
  さまざまなキーワードを試すことによって，より性能を高めていけるのではないかと考えられる．<br>
  また，キーワード検索ではなく，リストを作成しそこからツイートを取得するという方法も，やや手間はかかるがより精度の高い抽出を
  行うことができると考えられるため，今後試してみたい有力な候補である．
  
- 複数ツイートの表示
  Twitter APIの仕様上，最大で100ツイートのデータまで検索結果として取得することができる．<br>
  しかし，今回は実装上の問題から1ツイートのデータの表示にとどめている．
  将来的には100ツイートまで表示可能としたい．
  
- リツイートの場合の対応
  漫画が掲載されているツイートは人気が出やすいためリツイートが多く，APIは最新のツイートから検索するものを用いているため，
  当然漫画を描いた人がした元ツイートよりもそれをリツイートしたものの方が圧倒的に検索に引っかかりやすい．<br>
  その際，ツイート投稿者として表示するべきは元ツイートの投稿者であり，テキストも元ツイートのものを用いるべきだと考えた．<br>
  なぜなら，そのほうがユーザに馴染んでいるからである．Twitterでは，リツイートの場合リツイートした人の名前は左上に小さく表示されるだけで，
  元ツイートの投稿者のプロフィール写真及び名前は元ツイートのままである．そのため，元ツイートの投稿者がわからなくなることはない．<br>
  ところが，APIによって返される投稿者の名前は，リツイートの場合リツイートした人となっている．
  今回は実装の問題上そのままとしたが，本来は元ツイートの投稿者の情報を表示するべきである．


## 参考にしたサイト
- https://zenn.dev/nemuki/articles/my-first-android-app#api-%E3%82%A2%E3%82%AF%E3%82%BB%E3%82%B9%E3%81%AE%E7%94%A8%E3%81%AE%E8%A8%98%E8%BF%B0%E3%82%92%E8%BF%BD%E5%8A%A0
- https://developer.twitter.com/en/docs/twitter-api/data-dictionary/object-model/media