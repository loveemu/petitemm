PetiteMM
========
[![Travis Build Status](https://travis-ci.org/loveemu/petitemm.svg?branch=master)](https://travis-ci.org/loveemu/petitemm)

PetiteMM は SMF (MIDI) を MML に変換するツールです。

特徴:

- `c12d12e12` のような3連符に対応
- 和音には未対応（単音のみが変換されます）
- トラック間のタイミングがずれない
- コントロールチェンジには未対応（変換時に無視されます）

使用方法
--------

1. [Java](http://java.com/download/) Runtime Environment をインストールする（インストール済みではない場合）
2. .mid ファイルを PetiteMM.bat にドラッグ＆ドロップすると、.mml ファイルが入力ディレクトリに保存される

`java -jar PetiteMM.jar (options) input.mid` で PetiteMM を手動で実行することもできます。

### オプション

|オプション           |引数             |説明                                                                         |
|---------------------|-----------------|-----------------------------------------------------------------------------|
|-o                   |[string]filename |出力MMLファイル名を指定します。                                              |
|--dots               |[int]count       |符点の最大数を指定します。-1 で無制限になります。 (default=-1)               |
|--timebase           |[int]TPQN        |MML の分解能を指定します。0 ならば入力値にあわせます。 (default=48)          |
|--input-timebase     |[int]TPQN        |入力シーケンスの分解能を指定します。0 ならば入力値にあわせます。 (default=0) |
|--quantize-precision |[int]length      |クオンタイズの精度（最小ノート長）を指定します. (例: 64分音符であれば64)     |
|--no-quantize        |n/a              |ノート長の調整を無効化する。結果が正確になる反面、複雑になる。               |
|--octave-reverse     |n/a              |オクターブ記号の効果を反転します。                                           |
|--use-triplet        |n/a              |可能であれば3連符記法を使用します. (出来がいまいち)                          |

スペシャルサンクス
------------------

- TinyMM: よく似たツール。これをなくして PetiteMM は生まれませんでした。
