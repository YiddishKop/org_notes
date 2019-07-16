(ns clojure-noob.core
  (:gen-class))

;; ========= 实现 reduce 函数 =========
(defn my-reduce
  ([func item col]
   (loop [temp-col col
          final-item item]
     (if (empty? temp-col)
       final-item
       (let [[head & tail] temp-col]
         (recur tail
                (func final-item head))))))
  ([func col]
   (my-reduce func 0 col)))
;; ==================================




;; ========= Smacking around hobbits =============
;; 一, 首先定义左半部分身体结构
(def asym-hobbit-body-parts [{:name "head" :size 3}
                                                {:name "left-eye" :size 1}
                                                {:name "left-ear" :size 1}
                                                {:name "mouth" :size 1}
                                                {:name "nose" :size 1}
                                                {:name "neck" :size 2}
                                                {:name "left-shoulder" :size 3}
                                                {:name "left-upper-arm" :size 3}
                                                {:name "chest" :size 10}
                                                {:name "back" :size 10}
                                                {:name "left-forearm" :size 3}
                                                {:name "abdomen" :size 6}
                                                {:name "left-kidney" :size 1}
                                                {:name "left-hand" :size 2}
                                                {:name "left-knee" :size 2}
                                                {:name "left-thigh" :size 4}
                                                {:name "left-lower-leg" :size 3}
                                                {:name "left-achilles" :size 1}
                                                {:name "left-foot" :size 2}])

;; 二, 复制左半部分身体结构到右半边
;;     这里可以使用三种方法, 最好的是第三种 reduce

                                        ;  (1) by recursive function
;; 这样设计程序有两个缺点:
;; (1) 无法接受空列表参数
;; (2) 逆向
;; => (1)
;; 一开始就对参数进行拆解, 就会出现这种情况
;; => (2)
;; 这是我一开始自己实现的例子, 由于之前学习 racket 的背景, 我习惯使用 cons + 递归来实现程序.
;; 但是在 clojure 中没有 cons, 只有 conj.两者存在区别:
;; (cons item col) vs. (conj col item) vs. (into col_to col_from)
;; 所以使用 conj + 递归 设计程序会使结果与输入给函数的集合桉树[逆向].
;; 后来考虑使用 into 来实现, 也是相同的问题, 会[逆向]
(defn my-asym1
  ;; 从只有左边的身体获取整个身体构件.
  ;; ---------
  ;; map -> map
  [[head & tail]]
  (if (empty? tail) ; <= 不合理(1)
    []
    (into (my-asym1 tail) ; <= 不合理(2)
          (set [head
                {:name (clojure.string/replace
                        (:name head)
                        #"^left-"
                        "right-")
                 :size (:size head)}]))))


                                        ;  (2) by loop-if-let-recur


(defn my-asym2
  ;; 从只有左边的身体获取整个身体构件.
  ;; ---------
  ;; map -> map
  [parts]
  (loop [asym-parts parts ; 如果不使用递归的调用, 这里需要拷贝一份函数的引用供 loop 使用
         final-parts []]; 不断地把其中一个集合的元素经过某种处理转移到另一个集合中
    (if (empty? asym-parts)
      final-parts
      (let [[head & tail] asym-parts] ; let 起到类似 scala 模式匹配的作用,仅仅是拆解集合
                                      ; 拆解集合元素为递归做准备
        (recur tail
               (into final-parts      ; 处理元素的步骤放在这个 recur 中, 并且将其与
                                      ; "放置到另一个集合"的步骤捆绑到一起了.
                                      ; into: col-A, col-B -> col-A
                     (set [head       ; set 存在的目的是为了去重那些不含 "left-" 的部分
                           {:name (clojure.string/replace (:name head) #"^left-" "right-")
                            :size (:size head)}])))))))


                                        ;  (3) by reduce

(defn match-part
  ;; 对符合 "^left-" 的 name 进行替换, 保留 size 不变
  ;;---------
  ;; map -> map
  [part]
  ({:name (clojure.string/replace (:name part) #"^left-" "right-")
    :size (:size part)}))

(defn asym-helper
  ;; 对那些没有 "left-" 的身体部分通过 set 进行去重
  ;; 然后通过 into 放进最终 vector 里.
  ;;---------
  ;; vector of maps, map -> vector of maps
  [col item]
  (into col
        (set [item
              (match-part item)])))

(def whole-body-parts
  ;; 给整个身体结构对应的整体 vector of map 起名字
  ;; 对于 reduce 的第一个函数参数的类型需要说明的是
  ;;
  ;;   fn:     A    ,   B    ->     A
  ;;           ^        ^           ^
  ;;           |        |           |
  ;;           +-+------------------+
  ;;             |      |
  ;;             |      |
  ;; (reduce fn sth [item1 item2 item3])
  ;;
  ;; 这个函数 fn 的第一参数和返回值类型应该与 sth 保持一致;
  ;; 这个函数 fn 的第二参数的类型应该与 vector 内的元素保持一致.
  ;;
  ;; 这里 asym-helper: vector of maps, map -> vector of maps
  (reduce asym-helper [] asym-hobbit-body-parts))


;; 二, 确定随机打击位置
;; 程序示意图位置: https://www.mathcha.io/editor/zymwXhElSdyhlLTxr

(defn get-accumulated-size
  ;; 计算真个 vector of map 中的 size 的总和
  ;;---------
  ;; vector of maps -> int
  [whole-body-parts]
  (loop [temp-parts whole-body-parts
         accumulated-size 0]
    (if (empty? temp-parts)
      accumulated-size
      (let [[head & tail] temp-parts]
        (recur tail (+ accumulated-size (:size head)))))))

(defn get-hit-map
  ;; 获取随机打击的身体部位
  ;;---------
  ;; vector of maps -> map
  [parts]
  (let [rand-num (rand (get-accumulated-size parts))]
    (loop [temp-parts parts
           accumulated-size 0] ; 需要被递归改变的量一定要出现在 loop 的变量声明中
      (if (> accumulated-size rand-num)
        (first temp-parts)
        (let [[head & tail] temp-parts] ; let 在与 recur 配合之前必须要对 loop 中的集合进行拆解
          (recur tail
                 (+ accumulated-size (:size head)))))))) ; recur 中放置的是具体的递归处理操作

;; 三, 运行,获取最终结果,亦即打击位置
(get-hit-map whole-body-parts)
